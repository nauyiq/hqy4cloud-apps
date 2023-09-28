package com.hqy.cloud.message.service.impl;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.message.cache.ImRelationshipCacheService;
import com.hqy.cloud.message.cache.ImUnreadCacheService;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.service.ImConversationOperationsService;
import com.hqy.cloud.message.service.ImFriendOperationsService;
import com.hqy.cloud.message.service.ImMessageOperationsService;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.entity.ImFriend;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImFriendApplicationTkService;
import com.hqy.cloud.message.tk.service.ImFriendTkService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.spring.SpringContextHolder;
import com.hqy.cloud.web.common.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.ACCEPT_FRIEND_MESSAGE_CONTENT;
import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_SYSTEM_MESSAGE_UNREAD_ID;

/**
 * ImFriendOperationsService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 14:23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImFriendOperationsServiceImpl implements ImFriendOperationsService {
    private final TransactionTemplate template;
    private final ImFriendTkService friendTkService;
    private final ImFriendApplicationTkService friendApplicationTkService;
    private final ImUnreadCacheService imUnreadCacheService;
    private final ImMessageOperationsService imMessageOperationsService;
    private final ImConversationTkService imConversationTkService;
    private final ImRelationshipCacheService relationshipCacheService;

    @Override
    public boolean addFriend(Long apply, Long receive, String remark) {
        List<Long> userIds = Arrays.asList(apply, receive);
        Map<Long, AccountProfileStruct> profileMap = AccountRpcUtil.getAccountProfileMap(userIds);
        if (profileMap.size() != userIds.size()) {
            log.warn("Failed execute to add friend, because user not found, userIds = {}.", userIds);
            return false;
        }
        AccountProfileStruct applyProfile = profileMap.get(apply);
        AccountProfileStruct receiveProfile = profileMap.get(receive);
        List<ImFriend> imFriends = ImFriend.addFriend(apply, receive, remark, applyProfile.nickname, receiveProfile.nickname);
        // 获取聊天会话
        Date now = new Date();
        ImConversation applyConversation;
        ImConversation receiverConversation;
        List<ImConversation> imConversations = imConversationTkService.queryConversations(apply, receive, false);
        if (CollectionUtils.isEmpty(imConversations)) {
            applyConversation = ImConversation.ofFriend(apply, receive, receiveProfile.nickname, now);
            receiverConversation = ImConversation.ofFriend(receive, apply, applyProfile.nickname, now);
        } else {
            Map<Long, ImConversation> map = imConversations.stream().collect(Collectors.toMap(ImConversation::getUserId, v -> v));
            applyConversation = buildConversation(map, apply, receive, receiveProfile);
            receiverConversation = buildConversation(map, receive, apply, applyProfile);
        }
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(applyConversation.getId() == null ? imConversationTkService.insert(applyConversation) : imConversationTkService.updateSelective(applyConversation),
                        "Failed execute receive insert conversations by addFriend.");
                AssertUtil.isTrue(receiverConversation.getId() == null ? imConversationTkService.insert(receiverConversation) : imConversationTkService.updateSelective(receiverConversation),
                            "Failed execute receive insert conversations by addFriend.");
                // 新增好友关系
                AssertUtil.isTrue(friendTkService.insertList(imFriends), "Failed execute receive insert friends by addFriend.");
                // 新增系统消息
                imMessageOperationsService.addSystemMessage(receive, apply, ACCEPT_FRIEND_MESSAGE_CONTENT, applyConversation.getId());
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });
        if (Boolean.TRUE.equals(execute)) {
            doSendAppendChatEvent(applyConversation, receiverConversation, imConversations);
            relationshipCacheService.addFriendRelationship(receive, apply, (StringUtils.isBlank(remark) || remark.equals(StringConstants.FALSE)) ? StringConstants.TRUE : remark);
            relationshipCacheService.addFriendRelationship(apply, receive, (StringUtils.isBlank(remark) || remark.equals(StringConstants.FALSE)) ? StringConstants.TRUE : remark);
            return true;
        }
        return false;
    }

    private void doSendAppendChatEvent(ImConversation applyConversation, ImConversation receiverConversation, List<ImConversation> imConversations) {
        ImConversationOperationsService operationsService = SpringContextHolder.getBean(ImConversationOperationsService.class);
        operationsService.sendAppendPrivateChatEvent(applyConversation, 1);
        operationsService.sendAppendPrivateChatEvent(receiverConversation, 0);
    }

    @Override
    public boolean isFriend(Long from, Long to) {
        Boolean result = relationshipCacheService.isFriend(from, to);
        if (result == null) {
            // cache not found friend relationship, search from db.
            boolean isFriend = false;
            ImFriend friend = ImFriend.of(from, to);
            friend = friendTkService.queryOne(friend);
            String remark;
            if (friend == null) {
                remark = StringConstants.FALSE;
            } else {
                remark = StringUtils.isBlank(friend.getRemark()) ? StringConstants.TRUE : friend.getRemark();
                isFriend = true;
            }
            relationshipCacheService.addFriendRelationship(from, to, remark);
            return isFriend;
        } else {
            return result;
        }
    }

    @Override
    public boolean removeFriend(Long from, Long to) {
        ImFriend fromFriend = ImFriend.of(from, to);
        fromFriend = friendTkService.queryOne(fromFriend);
        if (fromFriend == null) {
            return true;
        }
        if (friendTkService.removeFriend(from, to)) {
            return Boolean.TRUE.equals(relationshipCacheService.removeFriend(from, to));
        }
        return false;
    }

    @Override
    public Map<Long, String> getFriendRemarks(Long id, List<Long> friendIds) {
        // query from redis
        List<String> friendRemarks = relationshipCacheService.getFriendRemarks(id, friendIds);
        Map<Long, String> resultMap = MapUtil.newHashMap(friendIds.size());
        List<Long> queryDbs = new ArrayList<>();
        for (int i = 0; i < friendRemarks.size(); i++) {
            String remark = friendRemarks.get(i);
            Long friendId = friendIds.get(i);
            if (StringUtils.isBlank(remark)) {
                queryDbs.add(id);
            } else if (!remark.equals(StringConstants.TRUE) && !remark.equals(StringConstants.FALSE)){
                resultMap.put(friendId, remark);
            }
        }
        if (CollectionUtils.isNotEmpty(queryDbs)) {
            // query from db.
            List<ImFriend> friends = friendTkService.queryFriends(id, queryDbs);
            if (CollectionUtils.isNotEmpty(friends)) {
                Map<Long, String> updateCache = new HashMap<>(friends.size());
                for (ImFriend friend : friends) {
                    Long friendUserId = friend.getUserId();
                    String remark = friend.getRemark();
                    if (StringUtils.isNotBlank(remark)) {
                        resultMap.put(friendUserId, remark);
                        updateCache.put(friendUserId, remark);
                    } else {
                        updateCache.put(friendUserId, StringConstants.TRUE);
                    }
                }
                relationshipCacheService.addFriendsRelationship(id, updateCache);
            }
        }
        return resultMap;
    }

    @Override
    public void updateApplicationStatus(Long userId, List<Long> applicationIds, int status) {
        if (friendApplicationTkService.updateApplicationStatus(applicationIds, status)) {
            imUnreadCacheService.readPrivateConversationUnread(userId, IM_SYSTEM_MESSAGE_UNREAD_ID);
        }
    }

    private ImConversation buildConversation(Map<Long, ImConversation> map, Long id, Long contactId, AccountProfileStruct contactProfile) {
        Date now = new Date();
        ImConversation conversation;
        if (map.containsKey(id)) {
            conversation = map.get(id);
            conversation.setUpdated(now);
            conversation.setRemove(null);
            conversation.setLastMessageTime(now);
            conversation.setLastMessageContent(contactProfile.nickname);
            conversation.setLastMessageType(ImMessageType.SYSTEM.type);
        } else {
            conversation = ImConversation.ofFriend(id, contactId, contactProfile.nickname, now);
        }
        return conversation;
    }


}
