package com.hqy.cloud.apps.blog.es.service;

import com.hqy.cloud.apps.blog.es.document.ArticleDoc;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.service.ElasticService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/4/19 14:39
 */
public interface ArticleElasticService extends ElasticService<Long, ArticleDoc> {

    /**
     * 分页查询Article.
     * @param title    模糊查询title
     * @param describe 模糊查询describe
     * @param current  当前页
     * @param size     一页几行
     * @return         result.
     */
    PageResult<ArticleDoc> queryPage(String title, String describe, Integer current, Integer size);


}
