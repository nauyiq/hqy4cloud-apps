package com.hqy.blog.service.impl;

import com.hqy.blog.service.ArticleTkService;
import com.hqy.blog.service.ArticleTypeTagsCompositeService;
import com.hqy.blog.service.TagsTkService;
import com.hqy.blog.service.TypeTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 15:07
 */
@Service
@RequiredArgsConstructor
public class ArticleTypeTagsCompositeServiceImpl implements ArticleTypeTagsCompositeService {

    private final ArticleTkService articleTkService;
    private final TagsTkService tagsTkService;
    private final TypeTkService typeTkService;

    @Override
    public ArticleTkService articleTkService() {
        return articleTkService;
    }

    @Override
    public TagsTkService tagsTkService() {
        return tagsTkService;
    }

    @Override
    public TypeTkService typeTkService() {
        return typeTkService;
    }
}
