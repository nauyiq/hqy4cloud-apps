package com.hqy.blog.statistics;

import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.fundation.cache.redis.key.support.DefaultKeyGenerator;

import java.util.List;

/**
 * StatisticsRedisService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 10:32
 */
public interface StatisticsRedisService<K,T> {

    DefaultKeyGenerator DEFAULT_KEY_GENERATOR = new DefaultKeyGenerator(MicroServiceConstants.BLOG_SERVICE);

    /**
     * 获取统计数据
     * @param key redis
     * @return statistics data.
     */
    T getStatistics(K key);

    List<T> getStatistics(List<K> keys);

    long countByType(K key, StatisticsType type);

    long incrValue(K key, StatisticsType type, int offset);
}
