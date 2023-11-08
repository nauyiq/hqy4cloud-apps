package com.hqy.cloud.apps.blog.service.statistics.support;

import com.hqy.cloud.apps.blog.dto.StatisticsDTO;
import com.hqy.cloud.apps.blog.service.statistics.StatisticsType;
import com.hqy.cloud.apps.blog.service.statistics.StatisticsTypeHashCache;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 采用hash结构, 为了减少内存使用 对文章id进行hash运算进行存储
 * 发生hash冲突时 具体解决思路看方法 this#genkey()
 * 注：无法保证一致性
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 16:52
 */
@Slf4j
@Service
public class ArticleStatisticsTypeHashCache implements StatisticsTypeHashCache<Long, StatisticsDTO> {

    /**
     * 所有文章的统计数据，redis key是不变的，但是需要注意如果数据量大时是否存在大key问题.
     */
    private final RMapCache<Long, StatisticsDTO> redisStatisticsCache;
    private final RedissonClient redissonClient;

    public ArticleStatisticsTypeHashCache(RedissonClient redissonClient) {
        this.redisStatisticsCache = redissonClient.getMapCache(getKey());
        this.redissonClient = redissonClient;
    }

    private int genKey(Long id) {
        int hash1 = id.hashCode();
        int hash2 = id.hashCode();
        StatisticsDTO o = redisStatisticsCache.get((long)hash1);
        if (o == null) {
            return hash1;
        } else {
            if (o.getId().equals(id)) {
                return hash1;
            }
        }

        int depth = 1;
        do {
            int newKey = hash1 + hash2 * depth;
            o = redisStatisticsCache.get((long)newKey);
            if (o == null) {
                return newKey;
            } else {
                if (o.getId().equals(id)) {
                    return newKey;
                }
            }
            depth++;

        } while (true);

    }

    @Override
    public StatisticsDTO getStatistics(Long id) {
        int hash1 = id.hashCode();
        int hash2 = id.hashCode();
        StatisticsDTO o = redisStatisticsCache.get((long)hash1);
        if (o == null) {
            return new StatisticsDTO(id, hash1);
        } else if (o.getId().equals(id)) {
            o.setKey(hash1);
            return o;
        } else {
            int depth = 1;
            do {
                int newKey = hash1 + hash2 * depth;
                o = redisStatisticsCache.get((long)newKey);
                if (o == null) {
                    return new StatisticsDTO(id, newKey);
                } else if (o.getId().equals(id)) {
                    o.setKey(newKey);
                    return o;
                } else {
                    depth++;
                }
            } while (true);
        }
    }

    @Override
    public List<StatisticsDTO> getStatistics(List<Long> keys) {
        AssertUtil.notEmpty(keys, "Id collection should not be empty.");

        //第一次hash获取key集合
        Set<Long> hashKeys = keys.stream().map(e -> (long)e.hashCode()).collect(Collectors.toSet());
        //查询redis
        Map<Long, StatisticsDTO> map = redisStatisticsCache.getAll(hashKeys);
        //结果集
        List<StatisticsDTO> resultList = new LinkedList<>(map.values());

        //获取第一次hash查询后查询不到的id集合， 之后单独查询
        Set<Long> ids = new HashSet<>(keys);
        Set<Long> statisticsIdCollection = map.values().stream().map(StatisticsDTO::getId).collect(Collectors.toSet());
        ids.removeAll(statisticsIdCollection);

        for (Long id : ids) {
            resultList.add(getStatistics(id));
        }

        return resultList;
    }


    @Override
    public void increment(Long id, StatisticsType type, int offset) {
        RLock lock = redissonClient.getLock(ArticleStatisticsTypeHashCache.class.getSimpleName() + id.toString());
        lock.lock();
        try {
            StatisticsDTO statistics = getStatistics(id);
            statistics.updateCount(type, offset);
            redisStatisticsCache.put((long)statistics.getKey(), statistics);
        } catch (Throwable cause) {
            log.error("Failed executed to incrValue. id {}, cause {}.", id, cause.getMessage());
        } finally {
            lock.unlock();
        }
    }

}
