package com.hqy.blog.service.request;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.blog.dto.PublishCommentDTO;

/**
 * CommentRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:44
 */
public interface CommentRequestService {

    /**
     * 获取分页评论列表
     * @param pageNumber 第几页？
     * @param pageSize   一页几行？
     * @return           DataResponse.
     */
    DataResponse getPageComments(Integer pageNumber, Integer pageSize);

    /**
     * 根据文章id 分页获取评论列表
     * @param articleId  文章id
     * @param pageNumber 第几页？
     * @param pageSize   一页几行？
     * @return
     */
    DataResponse getArticlePageComments(Long articleId, Integer pageNumber, Integer pageSize);

    /**
     * 发表评论
     * @param publishComment  {@link PublishCommentDTO}
     * @param accessAccountId commenter id.
     * @return
     */
    MessageResponse publishComment(PublishCommentDTO publishComment, Long accessAccountId);

    /**
     * 删除评论
     * @param accessAccountId 用户id
     * @param commentId       评论id.
     * @return                MessageResponse.
     */
    MessageResponse deleteComment(Long accessAccountId, Long commentId);
}
