package com.hqy.cloud.message.cache;

import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.cache.redis.key.RedisKey;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;

/**
 * using redis cache.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/28 10:54
 */
public abstract class ImCache {
    protected final RedisKey key;
    public ImCache(String name) {
        this.key = new RedisNamedKey(MicroServiceConstants.MESSAGE_NETTY_SERVICE, name);
    }
}
