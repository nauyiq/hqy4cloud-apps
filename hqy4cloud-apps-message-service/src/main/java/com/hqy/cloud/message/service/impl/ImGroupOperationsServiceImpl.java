package com.hqy.cloud.message.service.impl;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.apps.commom.constants.AppsConstants;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.enums.GroupRole;
import com.hqy.cloud.message.bind.event.support.*;
import com.hqy.cloud.message.bind.vo.ContactVO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.cache.ImRelationshipCacheService;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.service.ImGroupOperationsService;
import com.hqy.cloud.message.service.ImMessageOperationsService;
import com.hqy.cloud.message.tk.entity.*;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImGroupMemberTkService;
import com.hqy.cloud.message.tk.service.ImGroupTkService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.web.common.AccountRpcUtil;
import com.hqy.cloud.web.common.UploadResult;
import com.hqy.cloud.web.upload.UploadFileService;
import com.hqy.cloud.web.upload.UploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_DEFAULT_GROUP_AVATAR;
import static com.hqy.cloud.util.ImageUtil.MAX_FILE_SIZE;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:37
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImGroupOperationsServiceImpl implements ImGroupOperationsService {

    private final TransactionTemplate template;
    private final ImEventListener eventListener;
    private final ImGroupTkService groupTkService;
    private final ImMessageOperationsService messageOperationsService;
    private final ImGroupMemberTkService groupMemberTkService;
    private final ImConversationTkService contactTkService;
    private final ImRelationshipCacheService relationshipCacheService;
    private final UploadFileService uploadFileService;

    @Override
    public boolean createGroup(Long id, GroupDTO createGroup, List<ImFriend> friends) {
        // 调用账号RPC查询用户账号profile.
        List<Long> userIds = createGroup.getUserIds();
        userIds.add(id);
        Map<Long, AccountProfileStruct> profileMap = AccountRpcUtil.getAccountProfileMap(userIds);
        if (MapUtil.isEmpty(profileMap) || profileMap.size() != userIds.size()) {
            log.warn("Account rpc not found profiles by ids:{}.", userIds);
            return false;
        }
        String avatar = getGroupAvatar(id, userIds, profileMap);
        String groupName = getGroupName(id, createGroup, profileMap);
        ImGroup group = ImGroup.of(groupName, id, avatar, new Date());
        List<ImConversation> conversations = template.execute(status -> {
            try {
                // insert group.
                AssertUtil.isTrue(groupTkService.insert(group), "Failed execute to insert group.");
                Long groupId = group.getId();
                // insert group members
                List<ImGroupMember> groupMembers = ImGroupMember.of(groupId, id, userIds);
                AssertUtil.isTrue(groupMemberTkService.insertList(groupMembers), "Failed execute to insert group members.");
                // insert conversation.
                List<ImConversation> insertConversations = ImConversation.ofGroup(groupId, userIds);
                AssertUtil.isTrue(contactTkService.insertList(insertConversations), "Failed execute to insert conversations by create group.");
                // 新增事件消息
                messageOperationsService.addSimpleMessage(id, groupId, true, null, friends.parallelStream().map(ImFriend::getUserId).toList(),
                        ImMessageType.EVENT ,profileMap.get(id).username + AppsConstants.Message.CREATOR_GROUP_EVENT_CONTENT);
                return insertConversations;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return null;
            }
        });

        if (CollectionUtils.isNotEmpty(conversations)) {
            sendAppendGroupChatEvent(id, true, group, conversations);
            return true;
        }
        return false;
    }

    private String getGroupName(Long id, GroupDTO createGroup, Map<Long, AccountProfileStruct> profileMap) {
        if (StringUtils.isBlank(createGroup.getName())) {
            //生成默认群聊名字
            List<Long> userIds = createGroup.getUserIds();
            if (userIds.size() >= MAX_FILE_SIZE) {
                userIds = userIds.stream().limit(MAX_FILE_SIZE - 1).collect(Collectors.toList());
            }
            List<String> names = userIds.stream().map(user -> profileMap.get(user).nickname).toList();
            return StringUtils.join(names, "、");
        } else {
            return createGroup.getName();
        }
    }

    private String getGroupAvatar(Long id, List<Long> userIds, Map<Long, AccountProfileStruct> profileMap) {
        List<String> generatorAvatarList = new ArrayList<>();
        generatorAvatarList.add(profileMap.get(id).avatar);
        for (Long userId : userIds) {
            if (generatorAvatarList.size() >= MAX_FILE_SIZE) {
                break;
            }
            if (userId.equals(id)) {
                continue;
            }
            generatorAvatarList.add(profileMap.get(userId).avatar);
        }
        UploadResponse response = uploadFileService.generateFile(generatorAvatarList, AppsConstants.Message.IM_DEFAULT_GROUP_AVATAR_FOLDER);
        UploadResult result = response.getResult();
        return result.isResult() ? result.getRelativePath() : IM_DEFAULT_GROUP_AVATAR;
    }

    /**
     * 发送新增群聊聊天事件
     * @param operator        操作者
     * @param group          群聊信息
     * @param conversations  群聊成员会话
     */
    private void sendAppendGroupChatEvent(Long operator, boolean isAdd, ImGroup group, List<ImConversation> conversations) {
        List<AppendChatEvent> appendChatEvents;
        if (isAdd) {
            appendChatEvents = conversations.parallelStream().map(conversation -> {
                Long userId = conversation.getUserId();
                if (userId.equals(operator)) {
                    ConversationVO conversationVO = buildConversationVO(group, conversation);
                    ContactVO contactVO = buildContactVO(group, conversation);
                    return AppendChatEvent.of(operator.toString(), conversationVO, contactVO);
                } else {
                    return AppendChatEvent.of(conversation.getUserId().toString(), buildConversationVO(group, conversation), null);
                }
            }).toList();
        } else {
            appendChatEvents = conversations.parallelStream().filter(conversation -> !conversation.getUserId().equals(operator))
                    .map(conversation ->  AppendChatEvent.of(conversation.getUserId().toString(), buildConversationVO(group, conversation), null)).toList();
        }
        boolean result = eventListener.onImAppendGroupChatEvent(appendChatEvents);
        if (log.isDebugEnabled()) {
            log.debug("Do send append group event, group id: {}, result:{}.", group.getId(), result);
        }
    }

    private ContactVO buildContactVO(ImGroup group, ImConversation conversation) {
        return ContactVO.builder()
                .id(group.getId().toString())
                .isGroup(true)
                .isNotice(conversation.getNotice())
                .isTop(conversation.getTop())
                .avatar(group.getAvatar())
                .displayName(group.getName())
                .index(group.getIndex())
                .isInvite(group.getInvite())
                .build();
    }

    private ConversationVO buildConversationVO(ImGroup group, ImConversation conversation) {
        return ConversationVO.builder()
                .conversationId(conversation.getId().toString())
                .id(conversation.getUserId().toString())
                .invite(group.getInvite())
                .unread(1)
                .creator(group.getCreator().toString())
                .isGroup(true)
                .isNotice(conversation.getNotice())
                .isTop(conversation.getTop())
                .role(group.getCreator().equals(conversation.getUserId()) ? GroupRole.CREATOR.role : GroupRole.COMMON.role)
                .displayName(group.getName())
                .avatar(group.getAvatar())
                .build();
    }

    @Override
    public boolean editGroup(AuthenticationInfo userInfo, GroupMemberDTO info, GroupDTO editGroup) {
        boolean editGroupName = true;
        boolean editNotice = true;
        String name = editGroup.getName();
        String notice = editGroup.getNotice();
        if (StringUtils.isBlank(name) || name.equals(info.getGroupName())) {
            editGroupName = false;
        }
        if (StringUtils.isBlank(notice) || notice.equals(info.getNotice())) {
            editNotice = false;
        }
        if (!editGroupName && !editNotice) {
            return true;
        }
        Long groupId = info.getGroupId();
        ImGroup group = ImGroup.of(groupId);
        if (editGroupName) {
            group.setName(name);
        }
        if (editNotice) {
            group.setNotice(notice);
        }
        if (groupTkService.updateSelective(group)) {
            Long id = userInfo.getId();
            String editor = userInfo.getName();
            List<ImGroupMember> groupMembers = groupMemberTkService.queryList(ImGroupMember.of(groupId));
            List<String> userIds = groupMembers.parallelStream().map(ImGroupMember::getUserId).filter(userId -> !userId.equals(id)).map(Objects::toString).toList();
            if (editGroupName) {
                eventListener.onContactNameChangeEvent(ContactNameChangeEvent.of(true, userIds, groupId.toString(), name, editor));
                messageOperationsService.addSimpleMessage(id, groupId, true, null,
                        null, ImMessageType.EVENT,  AppsConstants.Message.IM_GROUP_NAME_CHANGE_CONTENT + name);
            }
            if (editNotice) {
                eventListener.onGroupNoticeChangeEvent(GroupNoticeEvent.of(userIds, groupId.toString(), notice, editor));
                messageOperationsService.addSimpleMessage(id, groupId, true, null,
                        null, ImMessageType.EVENT, AppsConstants.Message.IM_GROUP_NOTICE_CHANGE_CONTENT);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean addGroupMember(GroupMemberDTO groupMember) {
        ImGroupMember imGroupMember = ImGroupMember.of(groupMember.getGroupId(), groupMember.getId(), groupMember.getGroupName(), GroupRole.COMMON.role);
        ImConversation contact = ImConversation.ofGroup(groupMember.getId(), groupMember.getGroupId());
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(groupMemberTkService.insert(imGroupMember), "Failed execute to insert group members.");
                AssertUtil.isTrue(contactTkService.insert(contact), "Failed execute to insert group contact.");
                return true;
            } catch (Throwable cause) {
                log.error(cause.getMessage());
                status.setRollbackOnly();
                return false;
            }
        });

        if (Boolean.TRUE.equals(execute)) {
            //TODO send socket messages
            return true;
        }
        return false;
    }

    @Override
    public boolean removeGroupMember(Long id, Long groupId) {
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(groupMemberTkService.delete(new ImGroupMember(groupId, id)), "Failed execute to delete group member.");
                AssertUtil.isTrue(contactTkService.delete(ImConversation.of(id, groupId, true)), "Failed execute to delete group contact.");
                return true;
            } catch (Throwable cause) {
                log.error(cause.getMessage());
                status.setRollbackOnly();
                return false;
            }
        });

        if (Boolean.TRUE.equals(execute)) {
            //TODO send socket messages
            return true;
        }

        return false;
    }

    @Override
    public boolean isGroupMember(Long id, Long groupId) {
        Boolean result = relationshipCacheService.isGroupMember(groupId, id);
        if (result == null) {
            // query from db.
            ImGroupMember member = groupMemberTkService.queryOne(new ImGroupMember(groupId, id));
            if (member == null) {
                return false;
            }
            relationshipCacheService.addGroupMemberRelationship(groupId, id, StringUtils.isBlank(member.getDisplayName()) ? StringConstants.TRUE: member.getDisplayName());
            return true;
        }
        return false;
    }

    @Override
    public Map<Long, String> getGroupMemberRemark(Long groupId, List<Long> members) {
        Map<Long, String> resultMap = MapUtil.newHashMap(members.size());
        List<String> groupRemarks = relationshipCacheService.getGroupRemarks(groupId, members);
        if (CollectionUtils.isEmpty(groupRemarks)) {
            return resultMap;
        }
        List<Long> queryDbs = new ArrayList<>();
        for (int i = 0; i < groupRemarks.size(); i++) {
            String remark = groupRemarks.get(i);
            Long memberId = members.get(i);
            if (StringUtils.isBlank(remark) ) {
                queryDbs.add(memberId);
            } else if (!remark.equals(StringConstants.TRUE) && !remark.equals(StringConstants.FALSE)) {
                resultMap.put(memberId, remark);
            }
        }
        //query from db and fresh redis cache.
        if (CollectionUtils.isNotEmpty(queryDbs)) {
            List<ImGroupMember> groupMembers = groupMemberTkService.queryGroupMembers(groupId, queryDbs);
            if (CollectionUtils.isNotEmpty(groupMembers)) {
                Map<Long, String> map = groupMembers.stream().collect(Collectors.
                        toMap(ImGroupMember::getUserId, member -> StringUtils.isBlank(member.getDisplayName()) ? StringConstants.TRUE : member.getDisplayName()));
                relationshipCacheService.addGroupMembersRelationship(groupId, map);
            }
        }
        return resultMap;
    }
}
