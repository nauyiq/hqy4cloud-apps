package com.hqy.blog.dao;

import com.hqy.base.BaseDao;
import com.hqy.blog.dto.PageArticleDTO;
import com.hqy.blog.entity.Article;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BlogDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:22
 */
@Repository
public interface ArticleDao extends BaseDao<Article, Long> {

    /**
     * 查询文章列表
     * @param type 文章类型
     * @return {@link PageArticleDTO}
     */
    List<PageArticleDTO> articles(@Param("type") Integer type);

}
