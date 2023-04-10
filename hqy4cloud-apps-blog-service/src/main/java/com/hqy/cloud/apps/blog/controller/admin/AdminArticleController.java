package com.hqy.cloud.apps.blog.controller.admin;

import com.hqy.cloud.apps.blog.dto.ArticleDTO;
import com.hqy.cloud.apps.blog.service.request.ArticleRequestService;
import com.hqy.cloud.apps.blog.vo.PageArticleVO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.web.global.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

import static com.hqy.cloud.common.result.ResultCode.ERROR_PARAM_UNDEFINED;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/3 9:16
 */
@Slf4j
@RestController
@RequestMapping("/admin/blog")
@RequiredArgsConstructor
public class AdminArticleController extends BaseController{

    private final ArticleRequestService articleRequestService;

    @GetMapping("/article/page")
    public R<PageResult<PageArticleVO>> pageAdminArticles(String title, String describe, Integer current, Integer size) {
        current = current == null ? 1 : current;
        size = size == null ? 10 : size;
        return articleRequestService.adminPageArticles(title, describe, current, size);
    }

    @PostMapping("/article")
    public R<Boolean> publishArticle(@Valid @RequestBody ArticleDTO articleDTO) {
        AssertUtil.notNull(articleDTO, "Article data should not be null.");
        articleDTO.setAuthor(getAccessAccountId());
        return articleRequestService.publishArticle(articleDTO);
    }

    @PutMapping("/article")
    public R<Boolean> editArticle(@Valid @RequestBody ArticleDTO articleDTO) {
        if (Objects.isNull(articleDTO) || Objects.isNull(articleDTO.getId())) {
            return R.failed(ERROR_PARAM_UNDEFINED);
        }
        return articleRequestService.editArticle(articleDTO);
    }

    @DeleteMapping("/article/{id}")
    public R<Boolean> deleteArticle(@PathVariable("id") Long id) {
        return articleRequestService.deleteArticle(id);
    }



}
