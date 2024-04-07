package com.hqy.cloud.message.cache.support;

import cn.hutool.core.convert.Convert;
import com.hqy.cloud.foundation.redis.support.SmartRedisManager;
import com.hqy.cloud.message.bind.enums.BlacklistState;
import com.hqy.cloud.message.cache.ImBlacklistCache;
import com.hqy.cloud.message.cache.ImCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/5
 */
@Slf4j
@Component
public class RedisImBlacklistCache extends ImCache implements ImBlacklistCache {

    public RedisImBlacklistCache() {
        super("blacklist");
    }

    @Override
    public BlacklistState getBlacklistState(Long firstId, Long secondId) {
        List<String> keys = List.of(genKey(firstId), genKey(secondId));
        String script = """
                local v1 = redis.call('SISMEMBER', KEYS[1], ARGV[1])
                if v1 == 1 then return 1
                else
                    local v2 = redis.call("SISMEMBER", KEYS[2], ARGV[2])
                    if v2 == 1 then return 2
                    end
                    return 0
                end""";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);
        Long state = SmartRedisManager.getInstance().getStringTemplate().execute(redisScript, keys, firstId.toString(), secondId.toString());
        return BlacklistState.of(Convert.toInt(state));
    }

    @Override
    public void addBlacklist(Long userId, Long blackId) {
        String key = genKey(userId);
        SmartRedisManager.getInstance().sAdd(key, blackId.toString());
    }

    @Override
    public void removeBlacklist(Long userId, Long blackId) {
        String key = genKey(userId);
        SmartRedisManager.getInstance().sRem(key, blackId.toString());
    }

    public String genKey(Long id) {
        return key.getKey(id.toString());
    }



}
