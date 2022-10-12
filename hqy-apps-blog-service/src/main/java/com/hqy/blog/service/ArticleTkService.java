package com.hqy.blog.service;

import com.hqy.base.BaseTkService;
import com.hqy.blog.dto.PageArticleDTO;
import com.hqy.blog.entity.Article;

import java.util.List;

/**
 * ArticleTkService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:21
 */
public interface ArticleTkService extends BaseTkService<Article, Long> {

    /**
     * 查询文章列表
     * @return {@link PageArticleDTO}
     */
    List<PageArticleDTO> articles();

}
