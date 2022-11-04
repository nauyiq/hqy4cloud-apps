package com.hqy.blog.statistics.support;

import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.blog.statistics.StatisticsRedisService;
import com.hqy.util.thread.NamedThreadFactory;
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
    protected final String name;
    protected final RedissonClient redissonClient;
    protected final String prefix;
    protected final RedissonMapCache<K, T> redissonMapCache;

    public StatisticsHashRedisService(int syncIntervalSeconds, String name, RedissonClient redissonClient) {
        this(syncIntervalSeconds, syncIntervalSeconds, name, redissonClient);
    }

    public StatisticsHashRedisService(int syncDelaySeconds, int syncIntervalSeconds, String name, RedissonClient redissonClient) {
        this.name = name;
        this.redissonClient = redissonClient;
        this.prefix = StatisticsHashRedisService.class.getSimpleName() + StringConstants.Symbol.COLON + name;
        this.redissonMapCache = (RedissonMapCache<K, T>)redissonClient.getMapCache(getRedisKey());
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(name));
        executorService.scheduleAtFixedRate(this::startSync, syncDelaySeconds, syncIntervalSeconds, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void startSync() {
        //同步redis数据只需要一个线程执行即可
        String key = DEFAULT_KEY_GENERATOR.genKey("syncLock", name);
        RLock lock = redissonClient.getLock(key);
        if (lock.tryLock()) {
            try {
                Map<K, T> statisticsFromRedis = redissonMapCache.readAllMap();
                if (MapUtils.isEmpty(statisticsFromRedis)) {
                    log.info("{}-executor sync redis key {}, result map is empty.", prefix, name);
                } else {
                    loadRedisStatisticsData2Db(statisticsFromRedis);
                }
            } finally {
                lock.unlock();
            }
        }

    }

    protected String getRedisKey() {
        return DEFAULT_KEY_GENERATOR.genPrefix(prefix);
    }

    public RedissonMapCache<K, T> getRedissonMapCache() {
        return redissonMapCache;
    }

    /**
     * 加载redis中的统计数据到db
     * @param statisticsFromRedis redis statistic data.
     */
    protected abstract void loadRedisStatisticsData2Db(Map<K, T> statisticsFromRedis);

}