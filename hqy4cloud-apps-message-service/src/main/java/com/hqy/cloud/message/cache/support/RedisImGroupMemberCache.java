package com.hqy.cloud.message.cache.support;

import com.hqy.cloud.foundation.redis.support.SmartRedisManager;
import com.hqy.cloud.message.cache.ImCache;
import com.hqy.cloud.message.cache.ImGroupMemberCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/5
 */
@Slf4j
@Component
public class RedisImGroupMemberCache extends ImCache implements ImGroupMemberCache {

    public RedisImGroupMemberCache() {
        super("group-member");
    }

    @Override
    public Boolean isGroupMember(Long id, Long groupId) {
        String key = genKey(groupId);
        return SmartRedisManager.getInstance().sIsMember(key, id.toString());
    }

    @Override
    public void addGroupMember(Long id, Long groupId) {
        String key = genKey(groupId);
        SmartRedisManager.getInstance().sAdd(key, id.toString());
    }

    @Override
    public void removeGroupMember(Long id, Long groupId) {
        String key = genKey(groupId);
        SmartRedisManager.getInstance().sRem(key, id.toString());
    }

    @Override
    public void removeGroupAllMembers(Long groupId) {
        String key = genKey(groupId);
        SmartRedisManager.getInstance().del(key);
    }

    @Override
    public Set<Long> getGroupMembers(Long groupId) {
        String key = genKey(groupId);
        Set<String> members = SmartRedisManager.getInstance().sMembers(key);
        return members == null ? null : members.stream().map(Long::parseLong).collect(Collectors.toSet());
    }

    @Override
    public void addGroupMembers(Long groupId, Set<Long> members) {
        String key = genKey(groupId);
        Object[] array = members.stream().map(Objects::toString).toArray();
        SmartRedisManager.getInstance().sAdd(key, array);
    }

    public String genKey(Long groupId) {
        return key.getKey(groupId.toString());
    }

}
