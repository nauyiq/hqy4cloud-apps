package com.hqy.blog.dao;

import com.hqy.base.BaseDao;
import com.hqy.blog.entity.Article;
import org.springframework.stereotype.Repository;

/**
 * BlogDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:22
 */
@Repository
public interface ArticleDao extends BaseDao<Article, Long> {



}
