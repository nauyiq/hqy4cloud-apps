package com.hqy.blog.service;

import com.hqy.base.BaseTkService;
import com.hqy.blog.dto.ArticleCommentDTO;
import com.hqy.blog.entity.Comment;

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
}
