package com.hqy.blog.statistics;

import java.util.List;

/**
 * StatisticsRedisService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 10:32
 */
public interface StatisticsRedisService<K,T> {

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
