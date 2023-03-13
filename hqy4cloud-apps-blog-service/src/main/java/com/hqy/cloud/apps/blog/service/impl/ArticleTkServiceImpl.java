package com.hqy.cloud.apps.blog.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.cloud.apps.blog.dto.PageArticleDTO;
import com.hqy.cloud.apps.blog.entity.Article;
import com.hqy.cloud.apps.blog.mapper.ArticleMapper;
import com.hqy.cloud.apps.blog.service.ArticleTkService;
import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.cloud.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.PERCENT;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleTkServiceImpl extends BaseTkServiceImpl<Article, Long> implements ArticleTkService {

    private final ArticleMapper articleMapper;

    @Override
    public BaseTkMapper<Article, Long> getTkMapper() {
        return articleMapper;
    }

    @Override
    public List<PageArticleDTO> articles(Integer type, Integer status) {
        return articleMapper.articles(type, status);
    }

    @Override
    public PageInfo<Article> pageArticles(String title, String describe, Integer current, Integer size) {
        PageHelper.startPage(current, size);
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleted", false);
        if (StringUtils.isNotBlank(title)) {
            criteria.andLike("title", PERCENT.concat(title).concat(PERCENT));
        }
        if (StringUtils.isNotBlank(describe)) {
            criteria.andLike("description", PERCENT.concat(describe).concat(PERCENT));
        }
        List<Article> articles = articleMapper.selectByExample(example);
        return new PageInfo<>(articles);
    }

    @Override
    public boolean deleteArticles(List<Article> articles) {
        List<Long> ids = articles.stream().map(Article::getId).collect(Collectors.toList());
        return articleMapper.deleteArticles(ids) > 0;
    }
}
