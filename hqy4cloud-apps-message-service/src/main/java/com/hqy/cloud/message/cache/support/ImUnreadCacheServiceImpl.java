package com.hqy.cloud.message.cache.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.foundation.cache.redis.support.RedisManager;
import com.hqy.cloud.message.cache.ImCache;
import com.hqy.cloud.message.cache.ImUnreadCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
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
    public Map<Long, Integer> privateConversationsUnread(Long userId, List<Long> toContactId) {
        if (userId == null || CollectionUtils.isEmpty(toContactId)) {
            return MapUtil.newHashMap();
        }
        String key = genConversationKey(userId);
        List<Integer> unreadMap = RedisManager.getInstance().hmGet(key, toContactId.stream().map(e -> (Object) e).collect(Collectors.toList()));
        Map<Long, Integer> map = MapUtil.newHashMap(toContactId.size());
        for (int i = 0; i < toContactId.size(); i++) {
            Integer unread = unreadMap.get(i);
            map.put(toContactId.get(i), unread == null ? 0 : unread);
        }
        return map;
    }

    @Override
    public void addPrivateConversationUnread(Long userId, Long toContactId, Long offset) {
        if (userId == null || toContactId == null) {
            log.warn("Ignore add private conversation unread, request params is null.");
            return;
        }
        if (offset == null) {
            offset = 1L;
        }
        String key = genConversationKey(userId);
        RedisManager.getInstance().hIncrBy(key, toContactId, offset);
    }

    @Override
    public void addPrivateConversationsUnread(Long userId, Map<Long, Long> unreadContacts) {
        if (userId == null || MapUtil.isEmpty(unreadContacts)) {
            log.warn("Ignore add private conversations unread, request params is null.");
            return;
        }
        String key = genConversationKey(userId);
        RedisManager.getInstance().getRedisTemplate().executePipelined((RedisCallback<Object>) connection -> {
            unreadContacts.forEach((contactId, value) -> {
                long unread = value == null ? 1L : value;
                connection.hashCommands().hIncrBy(key.getBytes(), contactId.toString().getBytes(), unread);
            });
            return null;
        });
    }

    @Override
    public void addPrivateConversationsUnread(Long userId, List<Long> contacts) {
        if (userId == null || CollectionUtils.isEmpty(contacts)) {
            log.warn("Ignore add private conversations unread, request params is null.");
            return;
        }
        String key = genConversationKey(userId);
        RedisManager.getInstance().getRedisTemplate().executePipelined((RedisCallback<Object>) connection -> {
            contacts.forEach(contactId -> connection.hashCommands().hIncrBy(key.getBytes(), contactId.toString().getBytes(), 1L));
            return null;
        });
    }

    @Override
    public void addPrivateConversationsUnreadByUserIds(List<Long> userIds, Long contact) {
        if (contact == null || CollectionUtils.isEmpty(userIds)) {
            log.warn("Ignore add private conversations unread, request params is null.");
            return;
        }
        RedisManager.getInstance().getRedisTemplate().executePipelined((RedisCallback<Object>) connection -> {
            userIds.forEach(userId -> {
                String key = genConversationKey(userId);
                connection.hashCommands().hIncrBy(key.getBytes(), contact.toString().getBytes(), 1L);
            });
            return null;
        });
    }

    @Override
    public void readPrivateConversationUnread(Long userId, Long toContactId) {
        if (userId == null || toContactId == null) {
            log.warn("Ignore remove private conversation unread, request params is null.");
            return;
        }
        String key = genConversationKey(userId);
        RedisManager.getInstance().hDel(key, toContactId);
    }

    @Override
    public Integer getPrivateConversationUnread(Long userId, Long toContactId) {
        if (userId == null || toContactId == null) {
            return null;
        }
        String key = genConversationKey(userId);
        return RedisManager.getInstance().hGet(key, toContactId);
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
