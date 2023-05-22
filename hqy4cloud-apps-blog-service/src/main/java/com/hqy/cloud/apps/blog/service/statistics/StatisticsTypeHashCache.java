package com.hqy.cloud.apps.blog.service.statistics;

import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;

import java.util.List;

/**
 * StatisticsTypeHashCache.
 * @see StatisticsType
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 10:32
 */
public interface StatisticsTypeHashCache<FIELD, TARGET> {

    /**
     * 获取hashmap redis的key
     * @return redis key
     */
    default String getKey() {
        return getKey(null);
    }

    /**
     * 获取hashmap的redis key
     * @param type 统计类型.
     * @return     redis key.
     */
    default String getKey(StatisticsType type) {
        RedisNamedKey namedKey = new RedisNamedKey(MicroServiceConstants.BLOG_SERVICE, StatisticsTypeHashCache.class.getSimpleName());
        if (type == null) {
            return namedKey.getKey();
        }
        return namedKey.getKey(type.name());
    }

    /**
     * 获取统计数据
     * @param key redis
     * @return statistics data.
     */
    TARGET getStatistics(FIELD key);

    /**
     * 批量获取统计同居
     * @param keys 批量keys
     * @return     统计数据.
     */
    List<TARGET> getStatistics(List<FIELD> keys);

    /**
     * 使某个文章的StatisticsType值自增，并且获取自增后的值
     * @param filed  文章FILED KEY
     * @param type   StatisticsType
     * @param offset 偏移量
     */
    void increment(FIELD filed, StatisticsType type, int offset);
}
