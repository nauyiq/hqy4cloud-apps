package com.hqy.blog.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.blog.dao.ArticleDao;
import com.hqy.blog.dto.PageArticleDTO;
import com.hqy.blog.entity.Article;
import com.hqy.blog.service.ArticleCommentCompositeService;
import com.hqy.blog.service.ArticleTkService;
import com.hqy.blog.service.CommentTkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:51
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCommentCompositeServiceImpl implements ArticleCommentCompositeService {

    private final ArticleTkService articleTkService;
    private final CommentTkService commentTkService;


    @Override
    public ArticleTkService articleTkService() {
        return articleTkService;
    }

    @Override
    public CommentTkService commentTkService() {
        return commentTkService;
    }
}
