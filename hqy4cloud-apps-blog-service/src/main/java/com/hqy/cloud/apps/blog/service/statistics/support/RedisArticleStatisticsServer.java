package com.hqy.cloud.apps.blog.service.statistics.support;

import cn.hutool.core.util.StrUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.cloud.apps.blog.dto.AccountAccessArticleStatusDTO;
import com.hqy.cloud.apps.blog.dto.StatisticsDTO;
import com.hqy.cloud.apps.blog.entity.Liked;
import com.hqy.cloud.apps.blog.service.statistics.ArticleStatisticsServer;
import com.hqy.cloud.apps.blog.service.statistics.StatisticsType;
import com.hqy.cloud.apps.blog.service.statistics.StatisticsTypeHashCache;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.hqy.cloud.apps.blog.service.statistics.StatisticsType.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/13 9:33
 */
@Slf4j
@Component
public class RedisArticleStatisticsServer implements ArticleStatisticsServer {

    private final RedissonClient redissonClient;
    private final StatisticsTypeHashCache<Long, StatisticsDTO> statisticsTypeHashCache;

    /**
     * 采用zset结构缓存了用户对某个博客文章的状态值，是否已读，是否点赞。value为blog文章id，其中不同的分数表示不同的意义
     */
    private final Cache<Long, RScoredSortedSet<Long>> accountForBlogStatusCache;
    private static final double ONLY_READ_SCORE = 1.0;
    private static final double ONLY_LIKE_SCORE = 2.0;
    private static final double READ_LIKE_SCORE = 3.0;

    public RedisArticleStatisticsServer(RedissonClient redissonClient, StatisticsTypeHashCache<Long, StatisticsDTO> statisticsTypeHashCache) {
        this.redissonClient = redissonClient;
        this.statisticsTypeHashCache = statisticsTypeHashCache;
        this.accountForBlogStatusCache = CacheBuilder.newBuilder().initialCapacity(1024).expireAfterWrite(1L, TimeUnit.HOURS).build();
    }

    private RScoredSortedSet<Long> getBlogStatusCache(Long accountId) {
        if (Objects.isNull(accountId)) {
            throw new UnsupportedOperationException();
        }
        RScoredSortedSet<Long> set = accountForBlogStatusCache.getIfPresent(accountId);
        if (Objects.isNull(set)) {
            set = this.redissonClient.getScoredSortedSet(genKey(accountId));
            accountForBlogStatusCache.put(accountId, set);
        }
        return set;
    }

    private String genKey(Long accountId) {
        return MicroServiceConstants.BLOG_SERVICE.concat(StrUtil.COLON).concat(this.getClass().getSimpleName()).concat(accountId.toString());
    }

    @Override
    public boolean status(Long accountId, StatisticsType type, Long articleId) {
        RScoredSortedSet<Long> cache = getBlogStatusCache(accountId);
        if (!cache.contains(articleId)) {
            return false;
        }
        Double score = cache.getScore(articleId);
        if (Objects.isNull(score)) {
            return false;
        }

        //根据score值判断状态
        if (score.equals(READ_LIKE_SCORE)) {
            return true;
        } else if (type == LIKES) {
            return score.equals(ONLY_LIKE_SCORE);
        } else if (type == VISITS) {
            return score.equals(ONLY_READ_SCORE);
        } else {
            log.warn("Unsupported statics type = {}.", type);
            return false;
        }
    }

    @Override
    public AccountAccessArticleStatusDTO status(Long accountId, Long articleId) {
        boolean likeStatus;
        boolean isRead;
        RScoredSortedSet<Long> cache = getBlogStatusCache(accountId);
        Double score = cache.getScore(articleId);
        if (!cache.contains(articleId) || Objects.isNull(score) || score.equals(READ_LIKE_SCORE)) {
            likeStatus = false;
            isRead = false;
        } else if (score.equals(ONLY_LIKE_SCORE)) {
            likeStatus = true;
            isRead = false;
        } else if (score.equals(ONLY_READ_SCORE)) {
            likeStatus = false;
            isRead = true;
        } else {
            throw new UnsupportedOperationException("Unsupported statics score = " + score);
        }
        return new AccountAccessArticleStatusDTO(isRead, likeStatus);
    }

    @Override
    public boolean updateStatus(Long accountId, StatisticsType type, Long articleId) {
        //不支持的统计类型.
        if (Objects.isNull(type) || type == COMMENTS) {
            return false;
        }
        RScoredSortedSet<Long> cache = getBlogStatusCache(accountId);
        Double score = cache.getScore(articleId);
        int offset;
        //根据分数和统计类型 获取当前需要更新的分数
        if (!cache.contains(articleId) || Objects.isNull(score)) {
            //zset中分数本身不存在的情况
            if (type == LIKES) {
                score = ONLY_LIKE_SCORE;
            } else {
                score = ONLY_READ_SCORE;
            }
            offset = 1;
        } else {
            //zset中分数本身不存在的情况 需要根据本身值再加上当前值判断需要更新的值
            if (score.equals(READ_LIKE_SCORE)) {
                score = type == LIKES ? ONLY_READ_SCORE : ONLY_LIKE_SCORE;
                offset = -1;
            } else if (score.equals(ONLY_READ_SCORE)) {
                if (type == LIKES) {
                    score = READ_LIKE_SCORE;
                    offset = 1;
                } else {
                    score = null;
                    offset = -1;
                }
            } else if (score.equals(ONLY_LIKE_SCORE)) {
                if (type == LIKES) {
                    score = null;
                    offset = -1;
                } else {
                    score = READ_LIKE_SCORE;
                    offset = 1;
                }
            } else {
                log.warn("文章状态zset中存储了不支持的分数 = {}， 文章id = {}, 用户id = {}.", score, articleId, accountId);
                score = null;
                offset = 0;
            }
        }

        if (offset != 0) {
            statisticsTypeHashCache.incrAndGet(articleId, type, offset);
        }

        if (score == null) {
            return cache.remove(articleId);
        } else {
            return cache.add(score, accountId);
        }
    }

    @Override
    public StatisticsDTO getStatistics(Long key) {
        return this.statisticsTypeHashCache.getStatistics(key);
    }


    @Override
    public List<StatisticsDTO> getStatistics(List<Long> keys) {
        return this.statisticsTypeHashCache.getStatistics(keys);
    }

}
