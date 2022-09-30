package com.hqy.blog.controller.admin;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.blog.dto.ArticleDTO;
import com.hqy.blog.service.request.ArticleRequestService;
import com.hqy.util.AssertUtil;
import com.hqy.util.OauthRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 14:43
 */
@Slf4j
@RestController
@RequestMapping("/admin/article")
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleRequestService articleRequestService;

    @PostMapping
    public DataResponse publishArticle(@Valid @RequestBody ArticleDTO articleDTO, HttpServletRequest request) {
        AssertUtil.notNull(articleDTO, "Article data should not be null.");
        articleDTO.setAuthor(OauthRequestUtil.idFromOauth2Request(request));
        return articleRequestService.ArticleRequestService(articleDTO);
    }





}
