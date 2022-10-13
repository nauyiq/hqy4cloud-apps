package com.hqy.blog.statistics.support;

import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.blog.dto.AccountAccessArticleStatusDTO;
import com.hqy.blog.statistics.AccountAccessArticleServer;
import com.hqy.blog.statistics.StatisticsRedisService;
import com.hqy.blog.statistics.StatisticsType;
import com.hqy.fundation.cache.redis.LettuceRedis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/13 9:33
 */
@Slf4j
@Component
public class RedisBitMapAccountAccessArticleServer implements AccountAccessArticleServer {

    public String genPrefix(StatisticsType type) {
        return StatisticsRedisService.DEFAULT_KEY_GENERATOR.
                genPrefix(AccountAccessArticleServer.class.getSimpleName() + StringConstants.Symbol.COLON + type.name());
    }

    public String genKey(Long accountId, StatisticsType type) {
        String prefix = genPrefix(type);
        return StatisticsRedisService.DEFAULT_KEY_GENERATOR.genKey(prefix, accountId + "");
    }


    @Override
    public boolean accessStatus(Long accountId, StatisticsType type, Long articleId) {
        String key = genKey(accountId, type);
        return LettuceRedis.getInstance().getBit(key, accountId);
    }

    @Override
    public AccountAccessArticleStatusDTO accessStatus(Long accountId, Long articleId) {
        boolean visitStatus = accessStatus(accountId, StatisticsType.VISITS, articleId);
        boolean likeStatus = accessStatus(accountId, StatisticsType.LIKES, articleId);
        return new AccountAccessArticleStatusDTO(visitStatus, likeStatus);
    }

    @Override
    public boolean changeAccessStatus(Long accountId, StatisticsType type, Long articleId, boolean status) {
        String key = genKey(accountId, type);
        return LettuceRedis.getInstance().setBit(key, articleId, status);
    }
}
