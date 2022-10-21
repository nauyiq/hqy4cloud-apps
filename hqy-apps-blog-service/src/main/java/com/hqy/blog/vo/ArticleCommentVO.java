package com.hqy.blog.vo;

import cn.hutool.core.date.DateUtil;
import com.hqy.blog.entity.Comment;
import lombok.*;

import java.util.List;

/**
 * ArticleCommentVO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/13 13:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentVO extends ParentArticleCommentVO {

    private List<ChildArticleCommentVO> children;

    public ArticleCommentVO(Comment comment, User user, List<ChildArticleCommentVO> childArticleCommentVOS) {
        setId(comment.getId().toString());
        setContent(comment.getContent());
        setCreated(DateUtil.formatDateTime(comment.getCreated()));
        setDeleted(comment.getDeleted());
        setCommenter(user);
        this.children = childArticleCommentVOS;
    }
}



