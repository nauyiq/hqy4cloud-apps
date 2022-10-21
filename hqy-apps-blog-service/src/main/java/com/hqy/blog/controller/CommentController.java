package com.hqy.blog.controller;

import com.hqy.apps.common.result.BlogResultCode;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.blog.service.request.CommentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:41
 */
@Service
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentRequestService commentRequestService;

    @GetMapping("/admin/comments")
    public DataResponse adminComments(Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageSize = pageSize == null ? 10 : pageSize;
        return commentRequestService.getPageComments(pageNumber, pageSize);
    }

    @GetMapping("/comments/{articleId}")
    public DataResponse getArticleComments(@PathVariable("articleId") Long articleId, Integer pageNumber, Integer pageSize) {
        if (articleId == null) {
            return BlogResultCode.dataResponse(BlogResultCode.INVALID_ARTICLE_ID);
        }
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageSize = pageSize == null ? 10 : pageSize;
        return commentRequestService.getArticlePageComments(articleId, pageNumber, pageSize);
    }





}
