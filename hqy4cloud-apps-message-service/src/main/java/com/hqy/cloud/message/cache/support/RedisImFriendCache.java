package com.hqy.cloud.message.cache.support;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.DateMeasureConstants;
import com.hqy.cloud.foundation.redis.support.SmartRedisManager;
import com.hqy.cloud.message.cache.ImCache;
import com.hqy.cloud.message.cache.ImFriendCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
@Component
public class RedisImFriendCache extends ImCache implements ImFriendCache {
    public RedisImFriendCache() {
        super("friend");
    }

    @Value("${im.cache.friend.expired:86400}")
    private Long cacheSeconds = DateMeasureConstants.ONE_DAY.getSeconds();

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        StringRedisTemplate redisTemplate = SmartRedisManager.getInstance().getStringTemplate();
        List<String> keys = List.of(genKey(userId, friendId), genKey(friendId, userId));
        String script = """
                local v1 = redis.call('get', KEYS[1])
                if
                v1 == 1 then return 1
                else
                    local v2 = redis.call('get', KEYS[2])
                    if v2 == 1 then
                        return 1
                    else
                        return 0
                    end
                    return 0
                end""";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptText(script);
        Long execute = redisTemplate.execute(redisScript, keys);
        return execute != null && execute.equals(1L);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String key = genKey(userId, friendId);
        SmartRedisManager.getInstance().set(key, 1, cacheSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        SmartRedisManager.getInstance().del(genKey(userId, friendId), genKey(friendId, userId));
    }

    private String genKey(Long userId, Long friendId) {
        return key.getKey(userId + StrUtil.COLON + friendId);
    }

}
