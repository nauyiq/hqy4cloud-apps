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
    DataResponse AdminArticleRequestService(ArticleDTO articleDTO);

    /**
     * 分页查询文章列表
     * @param pageNumber 第几页
     * @param pageSize   一页几行
     * @return           DataResponse.
     */
    DataResponse pageArticles(Integer pageNumber, Integer pageSize);

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
