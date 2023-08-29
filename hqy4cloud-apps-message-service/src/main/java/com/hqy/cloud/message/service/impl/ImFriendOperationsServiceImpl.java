package com.hqy.cloud.message.service.impl;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.message.cache.ImRelationshipCacheService;
import com.hqy.cloud.message.service.ImFriendOperationsService;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.entity.ImFriend;
import com.hqy.cloud.message.tk.entity.ImFriendApplication;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImFriendTkService;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final ImConversationTkService imConversationTkService;
    private final ImRelationshipCacheService relationshipCacheService;

    @Override
    public boolean addFriend(ImFriendApplication application) {
        AssertUtil.notNull(application, "ImFriendApplication should not be null.");
        Long from = application.getId();
        Long to = application.getUserId();
        List<ImFriend> imFriends = ImFriend.of(from, to, application.getRemark());
        List<ImConversation> conversations = ImConversation.ofFriend(from, to, application.getRemark());
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(imConversationTkService.insertList(conversations), "Failed execute to insert conversations by addFriend.");
                AssertUtil.isTrue(friendTkService.insertList(imFriends), "Failed execute to insert friends by addFriend.");
                return relationshipCacheService.addFriendRelationship(from, to, null);
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return false;
            }
        });
        return Boolean.TRUE.equals(execute);
    }

    @Override
    public boolean isFriend(Long from, Long to) {
        Boolean result = relationshipCacheService.isFriend(from, to);
        if (result == null) {
            // cache not found friend relationship, search from db.
            boolean isFriend = false;
            ImFriend friend = ImFriend.of(from, to, true);
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
        }
        return result;
    }

    @Override
    public boolean removeFriend(Long from, Long to) {
        ImFriend fromFriend = ImFriend.of(from, to, true);
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


}
