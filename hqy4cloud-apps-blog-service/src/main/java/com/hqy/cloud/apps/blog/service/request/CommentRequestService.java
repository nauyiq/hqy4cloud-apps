package com.hqy.cloud.apps.blog.service.request;

import com.hqy.cloud.apps.blog.dto.PublishCommentDTO;
import com.hqy.cloud.apps.blog.vo.AdminPageCommentsVO;
import com.hqy.cloud.apps.blog.vo.ParentArticleCommentVO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;

/**
 * CommentRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:44
 */
public interface CommentRequestService {

    /**
     * 获取分页评论列表
     * @param articleId  模糊查询-文章id
     * @param content    模糊查询-评论内容
     * @param pageNumber 第几页？
     * @param pageSize   一页几行？
     * @return           R.
     */
    R<PageResult<AdminPageCommentsVO>> getPageComments(Long articleId, String content, Integer pageNumber, Integer pageSize);

    /**
     * 根据文章id 分页获取评论列表
     * @param articleId  文章id
     * @param pageNumber 第几页？
     * @param pageSize   一页几行？
     * @return           R.
     */
    R<PageResult<ParentArticleCommentVO>> getArticlePageComments(Long articleId, Integer pageNumber, Integer pageSize);

    /**
     * 发表评论
     * @param publishComment  {@link PublishCommentDTO}
     * @param accessAccountId commenter id.
     * @return                R.
     */
    R<Boolean> publishComment(PublishCommentDTO publishComment, Long accessAccountId);

    /**
     * 删除评论
     * @param accessAccountId 用户id
     * @param commentId       评论id.
     * @return                MessageResponse.
     */
    R<Boolean> deleteComment(Long accessAccountId, Long commentId);
}
