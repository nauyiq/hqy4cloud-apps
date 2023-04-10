package com.hqy.cloud.apps.blog.service;

import com.hqy.cloud.apps.blog.entity.Article;

/**
 * BlogDbOperationService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:50
 */
public interface BlogDbOperationService {

    /**
     * 删除文章
     * @param article 文章实体
     * @return         result.
     */
    boolean deleteArticle(Article article);


    LikedTkService likedTkService();

    TagsTkService tagsTkService();

    TypeTkService typeTkService();

    CommentTkService commentTkService();

    ArticleTkService articleTkService();

    ConfigTkService configTkService();



}
