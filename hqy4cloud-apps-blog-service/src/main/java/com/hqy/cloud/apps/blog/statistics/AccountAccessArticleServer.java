package com.hqy.cloud.apps.blog.statistics;

import com.hqy.cloud.apps.blog.dto.AccountAccessArticleStatusDTO;

/**
 * AccountAccessArticleService。
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/13 9:14
 */
public interface AccountAccessArticleServer {

    /**
     * 是否点赞或访问（读）
     * @param accountId 账号id
     * @param type      类型
     * @param articleId 文章id
     * @return
     */
    boolean accessStatus(Long accountId, StatisticsType type, Long articleId);

    /**
     * 用户对文章的状态
     * @param accountId 账号id
     * @param articleId 文章id
     * @return
     */
    AccountAccessArticleStatusDTO accessStatus(Long accountId, Long articleId);

    /**
     * 修改用户对文章的状态
     * @param accountId 账号id
     * @param type      类型
     * @param articleId 文章id
     * @param status    状态
     * @return
     */
    boolean changeAccessStatus(Long accountId, StatisticsType type,  Long articleId, boolean status);


}
