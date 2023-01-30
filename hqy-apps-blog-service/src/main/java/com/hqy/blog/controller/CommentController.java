package com.hqy.blog.controller;

import com.hqy.apps.common.result.BlogResultCode;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.dto.PublishCommentDTO;
import com.hqy.blog.service.request.CommentRequestService;
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
    public DataResponse getArticleComments(@PathVariable("articleId") Long articleId, Integer pageNumber, Integer pageSize) {
        if (articleId == null) {
            return BlogResultCode.dataResponse(BlogResultCode.INVALID_ARTICLE_ID);
        }
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageSize = pageSize == null ? 10 : pageSize;
        return commentRequestService.getArticlePageComments(articleId, pageNumber, pageSize);
    }

    @PostMapping("/article/comment")
    public MessageResponse publishComment(@RequestBody @Valid PublishCommentDTO publishComment, HttpServletRequest request) {
        Long accessAccountId = getAccessAccountId(request);
        if (accessAccountId == null) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_ACCESS_TOKEN);
        }
        // 二级评论.
        if (publishComment.getLevel() == 2) {
            if (StringUtils.isBlank(publishComment.getParentId())) {
                return CommonResultCode.messageResponse(CommonResultCode.ERROR_PARAM);
            }
        }

        return commentRequestService.publishComment(publishComment, accessAccountId);
    }


    @DeleteMapping("/comment/{commentId}")
    public MessageResponse deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        Long accessAccountId = getAccessAccountId(request);
        if (accessAccountId == null) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_ACCESS_TOKEN);
        }
        return commentRequestService.deleteComment(accessAccountId, commentId);
    }







}
