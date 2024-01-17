package com.hqy.cloud.message.cache.support;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.foundation.redis.support.SmartRedisManager;
import com.hqy.cloud.message.cache.ImCache;
import com.hqy.cloud.message.cache.ImRelationshipCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/28 17:20
 */
@Slf4j
@Service
public class ImRelationshipCacheServiceImpl extends ImCache implements ImRelationshipCacheService {
    private static final String GROUP_MEMBER_REMARK_PREFIX = "GROUP";

    public ImRelationshipCacheServiceImpl() {
        super("IM_RELATIONSHIP");
    }

    @Override
    public Boolean addFriendRelationship(Long from, Long to, String remark) {
        String key = genKey(from, to);
        return SmartRedisManager.getInstance().set(key, StringUtils.isBlank(remark) ? StringConstants.TRUE : remark, 7L, TimeUnit.DAYS);
    }

    @Override
    public void addFriendsRelationship(Long userId, Map<Long, String> relationship) {
        SmartRedisManager.getInstance().getStringTemplate().executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection redisConnection = (StringRedisConnection) connection;
            for (Map.Entry<Long, String> entry : relationship.entrySet()) {
                Long friendId = entry.getKey();
                String remark = entry.getValue();
                redisConnection.set(genKey(userId, friendId), remark);
            }
            return null;
        });
    }

    @Override
    public void addGroupMemberRelationship(Long groupId, Long userId, String remark) {
        String key = genGroupKey(groupId);
        SmartRedisManager.getInstance().hSet(key, userId.toString(), remark);
    }

    @Override
    public void addGroupMembersRelationship(Long groupId, Map<Long, String> remarks) {
        String key = genGroupKey(groupId);
        SmartRedisManager.getInstance().hmSet(key, remarks.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue)));
    }

    @Override
    public Boolean isFriend(Long from, Long to) {
        String key = genKey(from, to);
        String value = SmartRedisManager.getInstance().get(key);
        if (StringUtils.isNotBlank(value)) {
            return !value.equals(StringConstants.FALSE);
        }
        key = genKey(to, from);
        value = SmartRedisManager.getInstance().get(key);
        if (StringUtils.isNotBlank(value)) {
            return !value.equals(StringConstants.FALSE);
        } else {
            return null;
        }
    }

    @Override
    public Boolean isGroupMember(Long groupId, Long memberId) {
        String key = genGroupKey(groupId);
        String s = SmartRedisManager.getInstance().hGet(key, memberId.toString());
        if (s == null) return null;
        return StringUtils.isNotBlank(s);
    }

    @Override
    public Boolean removeGroupMember(Long groupId, Long memberId) {
        String key = genGroupKey(groupId);
        SmartRedisManager.getInstance().hDel(key, memberId.toString());
        return true;
    }

    @Override
    public Boolean removeGroup(Long groupId) {
        String key = genGroupKey(groupId);
        return SmartRedisManager.getInstance().del(key);
    }

    @Override
    public String getFriendRemark(Long userId, Long friendId) {
        String key = genKey(userId, friendId);
        String value = SmartRedisManager.getInstance().get(key);
        if (StringUtils.isBlank(value)) {
            return StrUtil.EMPTY;
        } else if (value.equals(StringConstants.FALSE) || value.equals(StringConstants.TRUE)) {
            return StrUtil.EMPTY;
        }
        return value;
    }

    @Override
    public List<String> getFriendRemarks(Long userId, List<Long> friendIds) {
        List<Object> remarks = SmartRedisManager.getInstance().getStringTemplate().executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection redisConnection = (StringRedisConnection) connection;
            friendIds.forEach(id -> redisConnection.get(genKey(userId, id)));
            return null;
        });
        return remarks.stream().map(remark -> remark == null ? null : (String) remark).toList();
    }

    @Override
    public List<String> getGroupRemarks(Long groupId, List<Long> groupMembers) {
        String key = genGroupKey(groupId);
        return SmartRedisManager.getInstance().hmGet(key, groupMembers.stream().map(member -> (Object) member.toString()).toList());
    }

    @Override
    public Boolean removeFriend(Long from, Long to) {
        String fromKey = genKey(from, to);
        String toKey = genKey(to, from);
        return SmartRedisManager.getInstance().del(fromKey, toKey);
    }

    private String genKey(Long from, Long to) {
        return this.key.getKey(from + StringConstants.Symbol.UNION + to);
    }

    private String genGroupKey(Long groupId) {
        return this.key.getKey(GROUP_MEMBER_REMARK_PREFIX + StringConstants.Symbol.UNION + groupId);
    }

}
