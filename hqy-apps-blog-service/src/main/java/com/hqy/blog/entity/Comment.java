package com.hqy.blog.entity;

import com.google.common.base.Objects;
import com.hqy.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Table;

/**
 * entity for t_comment.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:26
 */
@Data
@Table(name = "t_comment")
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity<Long> {

    /**
     * 博客id
     */
    private Long articleId;

    /**
     * 回复谁？
     */
    private Long replyId;

    /**
     * 内容
     */
    private String content;

    /**
     * 1 or 2
     */
    private Integer level;

    /**
     * 状态
     */
    private Boolean status;


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("articleId", articleId)
                .append("replyId", replyId)
                .append("content", content)
                .append("level", level)
                .append("status", status)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Comment comment = (Comment) o;
        return Objects.equal(articleId, comment.articleId) && Objects.equal(replyId, comment.replyId) && Objects.equal(content, comment.content) && Objects.equal(level, comment.level) && Objects.equal(status, comment.status);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), articleId, replyId, content, level, status);
    }
}
