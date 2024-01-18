package com.hqy.cloud.apps.blog.service.opeations;

import com.hqy.cloud.apps.blog.entity.Article;
import com.hqy.cloud.apps.blog.service.tk.ArticleTkService;
import com.hqy.cloud.apps.blog.service.tk.CommentTkService;
import com.hqy.cloud.apps.blog.service.tk.ConfigTkService;
import com.hqy.cloud.apps.blog.service.tk.TypeTkService;

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

    TypeTkService typeTkService();

    CommentTkService commentTkService();

    ArticleTkService articleTkService();

    ConfigTkService configTkService();



}
