package com.hqy.blog.controller;

import com.hqy.apps.common.result.BlogResultCode;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.service.request.ArticleRequestService;
import com.hqy.web.global.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 14:43
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog")
public class ArticleController extends BaseController {

    private final ArticleRequestService articleRequestService;

    @GetMapping("/articles")
    public DataResponse pageArticles(Integer type, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageSize = pageSize == null ? 5 : pageSize;
        return articleRequestService.pageArticles(type, pageNumber, pageSize, 1);
    }

    @GetMapping("/article/{id}")
    public DataResponse articleDetail(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null) {
            return BlogResultCode.dataResponse(BlogResultCode.INVALID_ARTICLE_ID);
        }
        Long accessAccountId = getAccessAccountId(request);
        return articleRequestService.articleDetail(accessAccountId, id);
    }

    @PostMapping("/article/like/{articleId}")
    public MessageResponse articleLiked(HttpServletRequest request, @PathVariable Long articleId) {
        if (articleId == null) {
            return BlogResultCode.dataResponse(BlogResultCode.INVALID_ARTICLE_ID);
        }
        Long accessAccountId = getAccessAccountId(request);
        if (accessAccountId == null) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_ACCESS_TOKEN);
        }
        return articleRequestService.articleLiked(accessAccountId, articleId);
    }

    @PostMapping("/article/read/{articleId}")
    public MessageResponse articleRead(@PathVariable Long articleId) {
        if (articleId == null) {
            return BlogResultCode.dataResponse(BlogResultCode.INVALID_ARTICLE_ID);
        }
        return articleRequestService.articleRead(articleId);
    }


}
