package com.hqy.cloud.apps.blog.service.impl;

import com.hqy.cloud.apps.blog.entity.Article;
import com.hqy.cloud.apps.blog.entity.Comment;
import com.hqy.cloud.apps.blog.service.*;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 10:51
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlogDbOperationServiceImpl implements BlogDbOperationService {

    private final TransactionTemplate transactionTemplate;
    private final LikedTkService likedTkService;
    private final ArticleTkService articleTkService;
    private final CommentTkService commentTkService;
    private final TagsTkService tagsTkService;
    private final TypeTkService typeTkService;
    private final ConfigTkService configTkService;


    @Override
    public boolean deleteArticle(Article article) {
        if (Objects.isNull(article)) {
            return false;
        }
        article.setDeleted(true);
        Long articleId = article.getId();
        List<Comment> comments = commentTkService.queryCommentsByArticleIds(Collections.singletonList(articleId));
        if (CollectionUtils.isEmpty(comments)) {
            return articleTkService.deleteArticles(Collections.singletonList(article));
        } else {
            Boolean result = transactionTemplate.execute(status -> {
                try {
                    AssertUtil.isTrue(articleTkService.deleteArticles(Collections.singletonList(article)), "Failed execute to delete article, id = " + articleId);
                    AssertUtil.isTrue(commentTkService.deleteComments(comments), "Failed execute to delete comments, id = " + article);
                    return true;
                } catch (Throwable cause) {
                    status.setRollbackOnly();
                    return false;
                }
            });
            return Boolean.TRUE.equals(result);
        }
    }

    @Override
    public LikedTkService likedTkService() {
        return likedTkService;
    }

    @Override
    public ArticleTkService articleTkService() {
        return articleTkService;
    }

    @Override
    public ConfigTkService configTkService() {
        return configTkService;
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
