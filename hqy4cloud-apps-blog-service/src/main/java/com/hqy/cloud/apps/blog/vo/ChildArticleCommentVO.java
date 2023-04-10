package com.hqy.cloud.apps.blog.vo;

import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.apps.blog.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/13 13:41
 */

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChildArticleCommentVO extends ArticleCommentVO {

    private User replier;

    public ChildArticleCommentVO(Comment comment, User commenter, User replier) {
        setId(comment.getId().toString());
        setContent(comment.getContent());
        setCreated(DateUtil.formatDateTime(comment.getCreated()));
        setDeleted(comment.getDeleted());
        setCommenter(commenter);
        setReplier(replier);
    }
}
