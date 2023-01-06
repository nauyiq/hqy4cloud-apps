package com.hqy.blog.controller.admin;

import com.hqy.apps.common.result.BlogResultCode;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.blog.service.request.CommentRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/4 14:40
 */
@Slf4j
@RestController
@RequestMapping("/admin/blog")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentRequestService commentRequestService;

    @GetMapping("/comment/page")
    public DataResponse adminComments(Long articleId, String content, Integer current, Integer size) {
        current = current == null ? 1 : current;
        size = size == null ? 20 : size;
        return commentRequestService.getPageComments(articleId, content, current, size);
    }

    @DeleteMapping("/comment/{id}")
    public MessageResponse deleteComment(@PathVariable("id") Long commentId) {
        if (Objects.isNull(commentId)) {
            return BlogResultCode.dataResponse(BlogResultCode.COMMENT_NOT_FOUND);
        }
        return commentRequestService.deleteComment(null, commentId);
    }



}
