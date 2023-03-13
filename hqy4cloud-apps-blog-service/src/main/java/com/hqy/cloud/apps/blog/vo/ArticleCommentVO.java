package com.hqy.cloud.apps.blog.vo;

import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.apps.blog.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/13 13:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentVO {

    private String id;
    private String content;
    private String created;
    private Boolean deleted;
    private User commenter;

    public ArticleCommentVO(Comment comment, User user) {
        this.id = comment.getId().toString();
        this.content = comment.getContent();
        this.created = DateUtil.formatDateTime(comment.getCreated());
        this.deleted = comment.getDeleted();
        this.commenter = user;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private String id;
        private String avatar;
        private String nickname;
    }


}
