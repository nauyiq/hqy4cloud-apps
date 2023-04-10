package com.hqy.cloud.apps.blog.statistics.support;

import com.hqy.cloud.apps.blog.service.StatisticsTkService;
import com.hqy.cloud.apps.blog.dto.StatisticsDTO;
import com.hqy.cloud.apps.blog.statistics.StatisticsType;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.cache.redis.key.support.RedisNamedKey;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO 后续可将统计功能包括点赞 浏览等功能提取出 新建一个服务.
 * 采用hash结构, 为了减少内存使用 对文章id进行hash运算进行存储
 * 发生hash冲突时 具体解决思路看方法 this#genkey()
 * 注：无法保证一致性
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 16:52
 */
@Slf4j
@Service
public class ArticleStatisticsHashRedisService extends StatisticsHashRedisService<Long, StatisticsDTO> {

    private final StatisticsTkService statisticsTkService;

    public ArticleStatisticsHashRedisService(RedissonClient redissonClient, StatisticsTkService statisticsTkService) {
        super(60, 60 * 60,
                new RedisNamedKey(MicroServiceConstants.BLOG_SERVICE, StatisticsDTO.class.getSimpleName()), redissonClient);
        this.statisticsTkService = statisticsTkService;
    }

    private int genKey(Long id) {
        int hash1 = id.hashCode();
        int hash2 = id.hashCode();
        StatisticsDTO o = getRedissonMapCache().get((long)hash1);
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
            o = getRedissonMapCache().get((long)newKey);
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
        StatisticsDTO o = getRedissonMapCache().get((long)hash1);
        if (o == null) {
            return new StatisticsDTO(id, hash1);
        } else if (o.getId().equals(id)) {
            o.setKey(hash1);
            return o;
        } else {
            int depth = 1;
            do {
                int newKey = hash1 + hash2 * depth;
                o = getRedissonMapCache().get((long)newKey);
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
        Map<Long, StatisticsDTO> map = getRedissonMapCache().getAll(hashKeys);
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
    public long countByType(Long id, StatisticsType type) {
        AssertUtil.notNull(type, "Statistics type should not be null.");
        StatisticsDTO statistics = getStatistics(id);
        return statistics.getCount(type);
    }

    @Override
    public long incrValue(Long id, StatisticsType type, int offset) {
        RLock lock = redissonClient.getLock(getRedisKey().getKey(id.toString()));
        lock.lock();
        try {
            StatisticsDTO statistics = getStatistics(id);
            statistics.updateCount(type, offset);
            getRedissonMapCache().put((long)statistics.getKey(), statistics);
            return statistics.getCount(type);
        } catch (Throwable cause) {
            log.error("Failed executed to incrValue. id {}, cause {}.", id, cause.getMessage());
            // TODO 补偿
            //...
            return 0;
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void loadRedisStatisticsData2Db(Map<Long, StatisticsDTO> statisticsFromRedis) {
        if (MapUtils.isEmpty(statisticsFromRedis)) {
            log.warn("Statistics map from redis is empty.");
            return;
        }
        Collection<StatisticsDTO> values = statisticsFromRedis.values();
        //TODO 1.如果REDIS中的数据已经很大 则全量读取HASH再会写到库明显不现实
        //     2.后续改造 冷热数据分离, 以时间节点（或者其他维度）来区分冷热数据, 冷数据读写都在库 热数据读写都在redis
        statisticsTkService.updateStatistics(values);
    }
}
