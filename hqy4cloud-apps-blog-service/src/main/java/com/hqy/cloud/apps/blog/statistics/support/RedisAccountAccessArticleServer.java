package com.hqy.cloud.apps.blog.statistics.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.apps.blog.dto.AccountAccessArticleStatusDTO;
import com.hqy.cloud.apps.blog.statistics.AccountAccessArticleServer;
import com.hqy.cloud.apps.blog.statistics.StatisticsType;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.cache.redis.key.RedisKey;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * RedisAccountAccessArticleServer.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/13 9:33
 */
@Slf4j
@Component
public class RedisAccountAccessArticleServer implements AccountAccessArticleServer {

    private final RedissonClient redissonClient;
    private final Map<StatisticsType, RedisKey> redisKeyMap;

    public RedisAccountAccessArticleServer(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.redisKeyMap = MapUtil.newConcurrentHashMap(3);
    }

    public String genKey(StatisticsType type) {
        return redisKeyMap.computeIfAbsent(type, key -> new RedisNamedKey(MicroServiceConstants.BLOG_SERVICE, type.name())).getKey();
    }

    @Override
    public boolean accessStatus(Long accountId, StatisticsType type, Long articleId) {
        String key = genKey(type);
        RSet<Long> set = redissonClient.getSet(key);
        return set.contains(articleId);
    }

    @Override
    public AccountAccessArticleStatusDTO accessStatus(Long accountId, Long articleId) {
        boolean likeStatus = accessStatus(accountId, StatisticsType.LIKES, articleId);
        return new AccountAccessArticleStatusDTO(false, likeStatus);
    }

    @Override
    public boolean changeAccessStatus(Long accountId, StatisticsType type, Long articleId, boolean status) {
        String key = genKey(type);
        RSet<Long> set = redissonClient.getSet(key);
        if (status) {
            return set.add(articleId);
        } else {
            return set.remove(articleId);
        }
    }


}
