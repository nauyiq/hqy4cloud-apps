package com.hqy.cloud.apps.blog.statistics.support;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.apps.blog.dto.AccountAccessArticleStatusDTO;
import com.hqy.cloud.apps.blog.statistics.StatisticsStatusService;
import com.hqy.cloud.apps.blog.statistics.StatisticsType;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.cache.redis.key.RedisKey;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
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
public class RedisStatisticsStatusServiceImpl implements StatisticsStatusService {

    private final RedissonClient redissonClient;
    private final Map<StatisticsType, RedisKey> redisKeyMap;

    public RedisStatisticsStatusServiceImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.redisKeyMap = MapUtil.newConcurrentHashMap(4);
    }

    public String genKey(StatisticsType type, Long articleId) {
        return redisKeyMap.computeIfAbsent(type, key -> new RedisNamedKey(MicroServiceConstants.BLOG_SERVICE, type.name().concat(StrUtil.COLON)
                .concat(articleId.toString()))).getKey();
    }


    @Override
    public boolean accessStatus(Long accountId, StatisticsType type, Long articleId) {
        String key = genKey(type, articleId);
        RScoredSortedSet<Long> sortedSet = redissonClient.getScoredSortedSet(key);
        return sortedSet.contains(articleId);
    }

    @Override
    public AccountAccessArticleStatusDTO accessStatus(Long accountId, Long articleId) {
        boolean likeStatus = accessStatus(accountId, StatisticsType.LIKES, articleId);
        boolean isRead = accessStatus(accountId, StatisticsType.VISITS, articleId);
        return new AccountAccessArticleStatusDTO(isRead, likeStatus);
    }

    @Override
    public boolean changeAccessStatus(Long accountId, StatisticsType type, Long articleId, boolean status) {
        String key = genKey(type, articleId);
        RScoredSortedSet<Long> sortedSet = redissonClient.getScoredSortedSet(key);
        if (status) {
            return sortedSet.add(System.currentTimeMillis(), articleId);
        } else {
            return sortedSet.remove(articleId);
        }
    }


}
