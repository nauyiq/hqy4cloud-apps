package com.hqy.cloud.apps.blog.statistics.support;

import com.hqy.cloud.apps.blog.statistics.StatisticsRedisService;
import com.hqy.cloud.foundation.cache.redis.key.RedisKey;
import org.apache.commons.collections4.MapUtils;
import org.redisson.RedissonMapCache;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * StatisticsHashRedisService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 10:47
 */
public abstract class StatisticsHashRedisService<K,T> implements StatisticsRedisService<K,T> {
    private static final Logger log = LoggerFactory.getLogger(StatisticsHashRedisService.class);
    private final RedisKey redisKey;
    protected final RedissonClient redissonClient;
    protected final RedissonMapCache<K, T> redissonMapCache;

    public StatisticsHashRedisService(int syncDelaySeconds, int syncIntervalSeconds, RedisKey redisKey, RedissonClient redissonClient) {
        this.redisKey = redisKey;
        this.redissonClient = redissonClient;
        this.redissonMapCache = (RedissonMapCache<K, T>)redissonClient.getMapCache(redisKey.getKey());
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::startSync, syncDelaySeconds, syncIntervalSeconds, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void startSync() {
        //同步redis数据只需要一个线程执行即可
        String key = redisKey.getKey("syncLock");
        RLock lock = redissonClient.getLock(key);
        if (lock.tryLock()) {
            try {
                Map<K, T> statisticsFromRedis = redissonMapCache.readAllMap();
                if (MapUtils.isEmpty(statisticsFromRedis)) {
                    log.info("Sync redis {}, result map is empty.", redisKey.getKey());
                } else {
                    loadRedisStatisticsData2Db(statisticsFromRedis);
                }
            } finally {
                lock.unlock();
            }
        }

    }

    public RedissonMapCache<K, T> getRedissonMapCache() {
        return redissonMapCache;
    }

    /**
     * 加载redis中的统计数据到db
     * @param statisticsFromRedis redis statistic data.
     */
    protected abstract void loadRedisStatisticsData2Db(Map<K, T> statisticsFromRedis);

    public RedisKey getRedisKey() {
        return redisKey;
    }
}
