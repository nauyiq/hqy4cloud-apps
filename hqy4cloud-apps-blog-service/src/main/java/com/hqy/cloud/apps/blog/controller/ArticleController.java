package com.hqy.cloud.apps.blog.controller;

import com.hqy.cloud.apps.blog.service.request.ArticleRequestService;
import com.hqy.cloud.apps.blog.vo.ArticleDetailVO;
import com.hqy.cloud.apps.blog.vo.PageArticleVO;
import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.web.global.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

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
    public R<PageResult<PageArticleVO>> pageArticles(Integer type, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? 1 : pageNumber;
        pageSize = pageSize == null ? 5 : pageSize;
        return articleRequestService.pageArticles(type, pageNumber, pageSize, 1);
    }

    @GetMapping("/article/{id}")
    public R<ArticleDetailVO> articleDetail(@PathVariable("id") Long id, HttpServletRequest request) {
        if (Objects.isNull(id)) {
            return R.failed(AppsResultCode.INVALID_ARTICLE_ID);
        }
        Long accessAccountId = getAccessAccountId(request);
        return articleRequestService.articleDetail(accessAccountId, id);
    }

    @PostMapping("/article/like/{articleId}")
    public R<Boolean> articleLiked(HttpServletRequest request, @PathVariable Long articleId) {
        if (Objects.isNull(articleId)) {
            return R.failed(AppsResultCode.INVALID_ARTICLE_ID);
        }
        Long accessAccountId = getAccessAccountId(request);
        if (Objects.isNull(accessAccountId)) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return articleRequestService.articleLiked(accessAccountId, articleId);
    }

    @PostMapping("/article/read/{articleId}")
    public R<Boolean> articleRead(@PathVariable Long articleId, HttpServletRequest request) {
        Long accessAccountId = getAccessAccountId(request);
        return articleRequestService.articleRead(articleId, accessAccountId);
    }


}
