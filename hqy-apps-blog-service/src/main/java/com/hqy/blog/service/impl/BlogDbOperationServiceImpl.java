package com.hqy.blog.service.impl;

import com.hqy.blog.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:51
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlogDbOperationServiceImpl implements BlogDbOperationService {

    private final LikedTkService likedTkService;
    private final ArticleTkService articleTkService;
    private final CommentTkService commentTkService;
    private final TagsTkService tagsTkService;
    private final TypeTkService typeTkService;


    @Override
    public LikedTkService likedTkService() {
        return likedTkService;
    }

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

    @Override
    public CommentTkService commentTkService() {
        return commentTkService;
    }
}
