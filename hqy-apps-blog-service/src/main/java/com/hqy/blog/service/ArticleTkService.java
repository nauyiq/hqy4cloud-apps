package com.hqy.blog.service;

import com.github.pagehelper.PageInfo;
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
     * @param type   文章类型
     * @param status 文章状态
     * @return {@link PageArticleDTO}
     */
    List<PageArticleDTO> articles(Integer type, Integer status);

    /**
     * 获取文章分页结果
     * @param title    模糊查询-标题
     * @param describe 模糊查询-描述
     * @param current  当前页
     * @param size     一页几行
     * @return         {@link PageInfo}
     */
    PageInfo<Article> pageArticles(String title, String describe, Integer current, Integer size);

    /**
     * 伪删除文章
     * @param articles 文章列表
     * @return         result
     */
    boolean deleteArticles(List<Article> articles);
}
