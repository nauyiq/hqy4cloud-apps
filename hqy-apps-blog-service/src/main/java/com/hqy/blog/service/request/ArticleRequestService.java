package com.hqy.blog.service.request;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.blog.dto.ArticleDTO;

/**
 * ArticleRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 15:46
 */
public interface ArticleRequestService {

    /**
     * 新增一遍文章.
     * @param articleDTO article data.
     * @return           DataResponse.
     */
    DataResponse publishArticle(ArticleDTO articleDTO);

    /**
     * 修改文章
     * @param articleDTO {@link ArticleDTO}
     * @return           DataResponse.
     */
    DataResponse editArticle(ArticleDTO articleDTO);

    /**
     * 删除文章
     * @param id 文章id
     * @return   DataResponse.
     */
    DataResponse deleteArticle(Long id);

    /**
     * 后台分页查询文章列表
     * @param title     模糊查询-标题
     * @param describe  模糊查询-描述
     * @param current   当前页
     * @param size      一页几行
     * @return          DataResponse.
     */
    DataResponse adminPageArticles(String title, String describe, Integer current, Integer size);

    /**
     * 分页查询文章列表
     * @param type       类型
     * @param pageNumber 第几页
     * @param pageSize   一页几行
     * @param status     文章状态
     * @return           DataResponse.
     */
    DataResponse pageArticles(Integer type, Integer pageNumber, Integer pageSize, Integer status);


    /**
     * 获取文章详情
     * @param accessAccountId 账号id
     * @param id              文章id
     * @return                DataResponse.
     */
    DataResponse articleDetail(Long accessAccountId, Long id);

    /**
     * 点赞/取消点赞 文章.
     * @param accessAccountId 用户id
     * @param articleId       文章id
     * @return                MessageResponse.
     */
    MessageResponse articleLiked(Long accessAccountId, Long articleId);

    /**
     * 文章阅读数 + 1
     * @param articleId 文章id
     * @return          MessageResponse.
     */
    MessageResponse articleRead(Long articleId);



}
