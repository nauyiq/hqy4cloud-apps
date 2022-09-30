package com.hqy.blog.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.blog.dao.ArticleDao;
import com.hqy.blog.entity.Article;
import com.hqy.blog.service.ArticleTkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleTkServiceImpl extends BaseTkServiceImpl<Article, Long> implements ArticleTkService {

    private final ArticleDao articleDao;

    @Override
    public BaseDao<Article, Long> selectDao() {
        return articleDao;
    }
}
