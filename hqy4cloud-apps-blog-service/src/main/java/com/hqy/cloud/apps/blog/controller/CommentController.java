package com.hqy.cloud.apps.blog.controller;

import com.hqy.cloud.apps.blog.dto.PublishCommentDTO;
import com.hqy.cloud.apps.blog.service.request.CommentRequestService;
import com.hqy.cloud.apps.blog.vo.ParentArticleCommentVO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.web.global.BaseController;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 文章评论相关接口.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:41
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog")
public class CommentController extends BaseController {

    private final CommentRequestService commentRequestService;

    @GetMapping("/comments/{articleId}")
    public R<PageResult<ParentArticleCommentVO>> getArticleComments(@PathVariable("articleId") Long articleId, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageSize = pageSize == null ? 10 : pageSize;
        return commentRequestService.getArticlePageComments(articleId, pageNumber, pageSize);
    }

    @PostMapping("/article/comment")
    public R<Boolean> publishComment(@RequestBody @Valid PublishCommentDTO publishComment, HttpServletRequest request) {
        Long accessAccountId = getAccessAccountId(request);
        // 二级评论.
        if (publishComment.getLevel() == 2) {
            if (StringUtils.isBlank(publishComment.getParentId())) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
        }
        return commentRequestService.publishComment(publishComment, accessAccountId);
    }


    @DeleteMapping("/comment/{commentId}")
    public R<Boolean> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        Long accessAccountId = getAccessAccountId(request);
        return commentRequestService.deleteComment(accessAccountId, commentId);
    }







}
