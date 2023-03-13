package com.hqy.cloud.apps.blog.controller;

import com.hqy.cloud.apps.blog.service.request.ArticleTypeRequestService;
import com.hqy.cloud.apps.blog.vo.ArticleTypeVO;
import com.hqy.cloud.common.bind.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 14:46
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog")
public class ArticleTypeController {

    private final ArticleTypeRequestService articleTypeRequestService;


    @GetMapping("/article/types")
    public R<List<ArticleTypeVO>> enableArticleTypes() {
        return articleTypeRequestService.enableArticleTypes();
    }


}
