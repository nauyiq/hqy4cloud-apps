package com.hqy.cloud.apps.blog.controller.admin;

import com.hqy.cloud.apps.blog.service.request.CommentRequestService;
import com.hqy.cloud.apps.blog.vo.AdminPageCommentsVO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public R<PageResult<AdminPageCommentsVO>> adminComments(Long articleId, String content, Integer current, Integer size) {
        current = current == null ? 1 : current;
        size = size == null ? 20 : size;
        return commentRequestService.getPageComments(articleId, content, current, size);
    }

    @DeleteMapping("/comment/{id}")
    public R<Boolean> deleteComment(@PathVariable("id") Long commentId) {
        return commentRequestService.deleteComment(null, commentId);
    }



}
