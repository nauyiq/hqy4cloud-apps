package com.hqy.cloud.apps.blog.vo;

import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.apps.blog.entity.Comment;
import lombok.*;

import java.util.List;

/**
 * ParentArticleCommentVO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/13 13:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ParentArticleCommentVO extends ArticleCommentVO {

    private List<ChildArticleCommentVO> replies;

    public ParentArticleCommentVO(Comment comment, User user, List<ChildArticleCommentVO> childArticleCommentVOS) {
        setId(comment.getId().toString());
        setContent(comment.getContent());
        setCreated(DateUtil.formatDateTime(comment.getCreated()));
        setDeleted(comment.getDeleted());
        setCommenter(user);
        this.replies = childArticleCommentVOS;
    }
}



