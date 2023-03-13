package com.hqy.cloud.apps.blog.service;

import com.github.pagehelper.PageInfo;
import com.hqy.cloud.apps.blog.dto.ArticleCommentDTO;
import com.hqy.cloud.apps.blog.entity.Comment;
import com.hqy.cloud.tk.BaseTkService;

import java.util.List;

/**
 * CommentTkService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:28
 */
public interface CommentTkService extends BaseTkService<Comment, Long> {

    /**
     * 查询父级评论列表
     * @param articleId 文章id
     * @return          Comments.
     */
    List<Comment> selectParentComments(Long articleId);

    /**
     * 查询子级评论列表
     * @param parents   一级评论id.
     * @param articleId  文章id
     * @return           ArticleCommentDTO.
     */
    List<ArticleCommentDTO> selectChildrenComments(List<Long> parents, Long articleId);

    /**
     * 查询评论的分页结果
     * @param articleId  文章id
     * @param content    内容
     * @param pageNumber 第几页
     * @param pageSize   一页几行
     * @return           分页结果.
     */
    PageInfo<Comment> queryPageComments(Long articleId, String content, Integer pageNumber, Integer pageSize);

    /**
     * 根据文章id集合查找评论列表
     * @param articleIds 文章id集合
     * @return           评论列表.
     */
    List<Comment> queryCommentsByArticleIds(List<Long> articleIds);

    /**
     * 伪删除评论列表
     * @param comments 待删除的评论列表
     * @return         result.
     */
    boolean deleteComments(List<Comment> comments);
}
