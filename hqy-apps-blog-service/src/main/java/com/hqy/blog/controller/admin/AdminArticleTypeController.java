package com.hqy.blog.controller.admin;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.blog.service.request.ArticleTypeRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 14:46
 */
@Slf4j
@RestController
@RequestMapping("/admin/article")
@RequiredArgsConstructor
public class AdminArticleTypeController {

    private final ArticleTypeRequestService articleTypeRequestService;

    @GetMapping("/types")
    public DataResponse articleTypes() {
        return articleTypeRequestService.articleTypes();
    }



}
