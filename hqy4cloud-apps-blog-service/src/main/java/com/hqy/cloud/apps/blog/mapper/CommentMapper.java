package com.hqy.cloud.apps.blog.mapper;

import com.hqy.cloud.apps.blog.dto.ArticleCommentDTO;
import com.hqy.cloud.apps.blog.entity.Comment;
import com.hqy.cloud.db.common.CreateTableSql;
import com.hqy.cloud.db.tk.BaseTkMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * CommentDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:29
 */
@Repository
public interface CommentMapper extends BaseTkMapper<Comment, Long> {

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

    @Insert("INSERT INTO t_comment(article_id,commenter,replier,content,level,parent,deleted,created,updated) VALUES" +
            "(#{articleId}, #{commenter}, #{replier}, #{content}, #{level}, #{parent}, #{deleted}, #{created}, #{updated})")
    int manualInsert(Comment comment);



    Map<String, String> selectTableCreateSql(@Param("tableName") String t_comment);
}
