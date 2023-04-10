package com.hqy.cloud.apps.blog.service.request;

import com.hqy.cloud.apps.blog.dto.ArticleDTO;
import com.hqy.cloud.apps.blog.vo.ArticleDetailVO;
import com.hqy.cloud.apps.blog.vo.PageArticleVO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;

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
     * @return           R.
     */
    R<Boolean> publishArticle(ArticleDTO articleDTO);

    /**
     * 修改文章
     * @param articleDTO {@link ArticleDTO}
     * @return           R.
     */
    R<Boolean> editArticle(ArticleDTO articleDTO);

    /**
     * 删除文章
     * @param id 文章id
     * @return   R.
     */
    R<Boolean> deleteArticle(Long id);

    /**
     * 后台分页查询文章列表
     * @param title     模糊查询-标题
     * @param describe  模糊查询-描述
     * @param current   当前页
     * @param size      一页几行
     * @return          R.
     */
    R<PageResult<PageArticleVO>> adminPageArticles(String title, String describe, Integer current, Integer size);

    /**
     * 分页查询文章列表
     * @param type       类型
     * @param pageNumber 第几页
     * @param pageSize   一页几行
     * @param status     文章状态
     * @return           R.
     */
    R<PageResult<PageArticleVO>> pageArticles(Integer type, Integer pageNumber, Integer pageSize, Integer status);


    /**
     * 获取文章详情
     * @param accessAccountId 账号id
     * @param id              文章id
     * @return                R.
     */
    R<ArticleDetailVO> articleDetail(Long accessAccountId, Long id);

    /**
     * 点赞/取消点赞 文章.
     * @param accessAccountId 用户id
     * @param articleId       文章id
     * @return                R.
     */
    R<Boolean> articleLiked(Long accessAccountId, Long articleId);

    /**
     * 文章阅读数 + 1
     * @param articleId 文章id
     * @return          R.
     */
    R<Boolean> articleRead(Long articleId);



}
