package com.hqy.cloud.message.cache.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.foundation.cache.redis.support.RedisManager;
import com.hqy.cloud.message.cache.ImCache;
import com.hqy.cloud.message.cache.ImUnreadCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/28 11:00
 */
@Slf4j
@Service
public class ImUnreadCacheServiceImpl extends ImCache implements ImUnreadCacheService {
    private static final String GROUP_UNREAD_PREFIX = "GROUP";
    public ImUnreadCacheServiceImpl() {
        super("IM_UNREAD");
    }

    @Override
    public Map<Long, Integer> privateConversationsUnread(Long userId, List<Long> conversations) {
        if (userId == null || CollectionUtils.isEmpty(conversations)) {
            return MapUtil.newHashMap();
        }
        String key = genConversationKey(userId);
        List<Integer> unreadMap = RedisManager.getInstance().hmGet(key, conversations.stream().map(e -> (Object) e).collect(Collectors.toList()));
        Map<Long, Integer> map = MapUtil.newHashMap(conversations.size());
        for (int i = 0; i < conversations.size(); i++) {
            Integer unread = unreadMap.get(i);
            map.put(conversations.get(i), unread == null ? 0 : unread);
        }
        return map;
    }

    @Override
    public void addPrivateConversationUnread(Long userId, Long conversationId, Long offset) {
        if (userId == null || conversationId == null) {
            log.warn("Ignore add private conversation unread, request params is null.");
            return;
        }
        if (offset == null) {
            offset = 1L;
        }
        String key = genConversationKey(userId);
        RedisManager.getInstance().hIncrBy(key, conversationId, offset);
    }

    @Override
    public void readPrivateConversationUnread(Long userId, Long conversationId) {
        if (userId == null || conversationId == null) {
            log.warn("Ignore remove private conversation unread, request params is null.");
            return;
        }
        String key = genConversationKey(userId);
        RedisManager.getInstance().hDel(key, conversationId);
    }

    @Override
    public Integer getPrivateConversationUnread(Long userId, Long conversationId) {
        if (userId == null || conversationId == null) {
            return null;
        }
        String key = genConversationKey(userId);
        return RedisManager.getInstance().hGet(key, conversationId);
    }

    @Override
    public Map<Long, Integer> groupConversationsUnread(Long userId, List<Long> groupIds) {
        if (userId == null || CollectionUtils.isEmpty(groupIds)) {
            return MapUtil.newHashMap();
        }
        List<Object> groupList = RedisManager.getInstance().getRedisTemplate().executePipelined((RedisCallback<Object>) connection -> {
            groupIds.forEach(id -> connection.hashCommands().hGet(genGroupUnreadKey(id).getBytes(), userId.toString().getBytes()));
            return null;
        });
        Map<Long, Integer> map = MapUtil.newHashMap(groupIds.size());
        for (int i = 0; i < groupIds.size(); i++) {
            Object o = groupList.get(i);
            int unread = 0;
            if (o instanceof Integer) {
                unread = (Integer) o;
            }
            map.put(groupIds.get(i), unread);
        }
        return map;
    }

    @Override
    public void addGroupConversationUnread(Long userId, Long groupId, Long offset) {
        if (userId == null || groupId == null) {
            log.warn("Ignore add group conversation unread, request params is null.");
            return;
        }
        if (offset == null) {
            offset = 1L;
        }
        String key = genGroupUnreadKey(groupId);
        RedisManager.getInstance().hIncrBy(key, userId, offset);
    }

    @Override
    public void addGroupConversationsUnread(Set<Long> userIds, Long groupId, Long offset) {
        if (CollectionUtils.isEmpty(userIds) || groupId == null) {
            return;
        }
        if (offset == null) {
            offset = 1L;
        }
        String key = genGroupUnreadKey(groupId);
        Long finalOffset = offset;
        RedisManager.getInstance().getRedisTemplate().executePipelined((RedisCallback<Object>) connection -> {
            userIds.forEach(member -> connection.hashCommands().hIncrBy(key.getBytes(), member.toString().getBytes(), finalOffset));
            return null;
        });
    }

    @Override
    public void readGroupConversationUnread(Long userId, Long groupId) {
        if (userId == null || groupId == null) {
            log.warn("Ignore remove group conversation unread, request params is null.");
            return;
        }
        String key = genGroupUnreadKey(groupId);
        RedisManager.getInstance().hDel(key, userId);
    }


    private String genConversationKey(Long userId) {
        return this.key.getKey(userId.toString());
    }

    private String genGroupUnreadKey(Long groupId) {
        return this.key.getKey(GROUP_UNREAD_PREFIX + StringConstants.Symbol.UNION + groupId);
    }

}
