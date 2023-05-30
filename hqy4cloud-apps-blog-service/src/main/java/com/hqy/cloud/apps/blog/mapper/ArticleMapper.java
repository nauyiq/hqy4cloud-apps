package com.hqy.cloud.apps.blog.mapper;

import com.hqy.cloud.apps.blog.dto.PageArticleDTO;
import com.hqy.cloud.apps.blog.entity.Article;
import com.hqy.cloud.db.tk.BaseTkMapper;
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
public interface ArticleMapper extends BaseTkMapper<Article, Long> {

    /**
     * 查询文章列表
     * @param type   文章类型
     * @param status 文章状态
     * @return {@link PageArticleDTO}
     */
    List<PageArticleDTO> articles(@Param("type") Integer type, @Param("status") Integer status);

    /**
     * 伪删除文章
     * @param ids id列表
     * @return    行数
     */
    long deleteArticles(@Param("ids") List<Long> ids);

}
