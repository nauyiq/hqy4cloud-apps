package com.hqy.blog.entity;

import com.hqy.base.BaseEntity;
import lombok.*;

import javax.persistence.Table;

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
    private Long commentId;

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

    /**
     * 是否删除
     */
    private Boolean deleted;

}
