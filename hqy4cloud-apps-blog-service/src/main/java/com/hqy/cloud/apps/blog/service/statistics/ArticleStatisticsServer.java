package com.hqy.cloud.apps.blog.service.statistics;

import com.hqy.cloud.apps.blog.dto.AccountAccessArticleStatusDTO;
import com.hqy.cloud.apps.blog.dto.StatisticsDTO;

import java.util.List;

/**
 * 文章数据统计.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/13 9:14
 */
public interface ArticleStatisticsServer {

    /**
     * 是否点赞或访问（读）
     * @param accountId 账号id
     * @param type      类型
     * @param articleId 文章id
     * @return          result
     */
    boolean status(Long accountId, StatisticsType type, Long articleId);

    /**
     * 获取这个用户是否点赞和阅读文章
     * @param accountId 账号id
     * @param articleId 文章id
     * @return          result
     */
    AccountAccessArticleStatusDTO status(Long accountId, Long articleId);

    /**
     * 修改用户对文章的状态
     * @param accountId 账号id
     * @param type      类型
     * @param articleId 文章id
     * @return          result
     */
    boolean updateStatus(Long accountId, StatisticsType type, Long articleId);


    /**
     * 获取统计数据
     * @param accountId   accountId
     * @return statistics data.
     */
    StatisticsDTO getStatistics(Long accountId);

    /**
     * 根据文章列表批量获取统计数据
     * @param keys 文章id列表
     * @return     批量统计数据
     */
    List<StatisticsDTO> getStatistics(List<Long> keys);


}
