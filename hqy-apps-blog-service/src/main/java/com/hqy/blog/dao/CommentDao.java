package com.hqy.blog.dao;

import com.hqy.base.BaseDao;
import com.hqy.blog.dto.ArticleCommentDTO;
import com.hqy.blog.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CommentDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:29
 */
@Repository
public interface CommentDao extends BaseDao<Comment, Long> {

    /**
     * 查询父级评论列表
     * @param articleId 文章id
     * @return          comments。
     */
    List<Comment> selectParentComments(@Param("articleId") Long articleId);

    /**
     * 查询子级评论列表
     * @param parents   一级评论id.
     * @param articleId 文章id.
     * @return          ArticleCommentDTO.
     */
    List<ArticleCommentDTO> selectChildrenComments(@Param("parents") List<Long> parents, @Param("articleId") Long articleId);

    /**
     * 伪删除评论列表
     * @param ids id集合
     * @return    行数
     */
    long deleteComments(@Param("ids") List<Long> ids);
}
