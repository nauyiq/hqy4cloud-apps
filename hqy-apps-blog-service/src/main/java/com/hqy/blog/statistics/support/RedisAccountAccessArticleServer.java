package com.hqy.blog.statistics.support;

import com.hqy.blog.dto.AccountAccessArticleStatusDTO;
import com.hqy.blog.statistics.AccountAccessArticleServer;
import com.hqy.blog.statistics.StatisticsRedisService;
import com.hqy.blog.statistics.StatisticsType;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

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

    public RedisAccountAccessArticleServer(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public String genKey(Long accountId, StatisticsType type) {
        return StatisticsRedisService.DEFAULT_KEY_GENERATOR.genKey(type.name(), accountId + "");
    }


    @Override
    public boolean accessStatus(Long accountId, StatisticsType type, Long articleId) {
        String key = genKey(accountId, type);
        RSet<Long> set = redissonClient.getSet(key);
        return set.contains(articleId);
    }

    @Override
    public AccountAccessArticleStatusDTO accessStatus(Long accountId, Long articleId) {
//        boolean visitStatus = accessStatus(accountId, StatisticsType.VISITS, articleId);
        boolean likeStatus = accessStatus(accountId, StatisticsType.LIKES, articleId);
        return new AccountAccessArticleStatusDTO(false, likeStatus);
    }

    @Override
    public boolean changeAccessStatus(Long accountId, StatisticsType type, Long articleId, boolean status) {
        String key = genKey(accountId, type);
        RSet<Long> set = redissonClient.getSet(key);
        if (status) {
            return set.add(articleId);
        } else {
            return set.remove(articleId);
        }
    }


}
