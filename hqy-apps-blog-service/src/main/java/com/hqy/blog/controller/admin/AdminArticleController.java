package com.hqy.blog.controller.admin;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.dto.ArticleDTO;
import com.hqy.blog.service.request.ArticleRequestService;
import com.hqy.util.AssertUtil;
import com.hqy.util.OauthRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.hqy.base.common.result.CommonResultCode.ERROR_PARAM_UNDEFINED;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/3 9:16
 */
@Slf4j
@RestController
@RequestMapping("/admin/blog")
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleRequestService articleRequestService;


    @GetMapping("/article/page")
    public DataResponse pageAdminArticles(String title, String describe, Integer current, Integer size) {
        current = current == null ? 1 : current;
        size = size == null ? 10 : size;
        return articleRequestService.adminPageArticles(title, describe, current, size);
    }

    @PostMapping("/article")
    public DataResponse publishArticle(@Valid @RequestBody ArticleDTO articleDTO, HttpServletRequest request) {
        AssertUtil.notNull(articleDTO, "Article data should not be null.");
        articleDTO.setAuthor(OauthRequestUtil.idFromOauth2Request(request));
        return articleRequestService.publishArticle(articleDTO);
    }

    @PutMapping("/article")
    public DataResponse editArticle(@Valid @RequestBody ArticleDTO articleDTO) {
        if (articleDTO == null || articleDTO.getId() == null) {
            return CommonResultCode.dataResponse(ERROR_PARAM_UNDEFINED);
        }
        return articleRequestService.editArticle(articleDTO);
    }

    @DeleteMapping("/article/{id}")
    public DataResponse deleteArticle(@PathVariable("id") Long id) {
        if (id == null) {
            return CommonResultCode.dataResponse(ERROR_PARAM_UNDEFINED);
        }
        return articleRequestService.deleteArticle(id);
    }



}
