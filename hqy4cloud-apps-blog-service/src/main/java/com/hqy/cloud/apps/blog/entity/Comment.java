package com.hqy.cloud.apps.blog.entity;

import com.hqy.cloud.tk.model.BaseEntity;
import lombok.*;

import javax.persistence.Table;
import java.util.Date;

/**
 * entity for t_comment.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:26
 */
@Data
@ToString
@Table(name = "t_comment")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity<Long> {

    /**
     * 博客id
     */
    private Long articleId;

    /**
     * 评论id
     */
    private Long commenter;

    /**
     * 回复谁？
     */
    private Long replier;

    /**
     * 内容
     */
    private String content;

    /**
     * 1 or 2
     */
    private Integer level;

    /**
     * 父级评论id.
     */
    private Long parent;


    /**
     * 是否删除
     */
    private Boolean deleted;

    public Comment(Long id, Long articleId, Long commenter, Long replier, String content, Integer level, Long parent) {
        super(id, new Date());
        this.articleId = articleId;
        this.commenter = commenter;
        this.replier = replier;
        this.content = content;
        this.level = level;
        this.parent = parent;
        this.deleted = false;
    }
}
