package com.hqy.cloud.apps.blog.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.util.Date;

/**
 * Liked.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/31 11:03
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_article_liked")
public class Liked extends BaseEntity<Long> {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 账号id
     */
    private Long accountId;

    /**
     * 状态
     */
    private Boolean status;

    public Liked(Long articleId, Long accountId) {
        this(articleId, accountId, true);
    }

    public Liked(Long articleId, Long accountId, Boolean status) {
        super(new Date());
        this.articleId = articleId;
        this.accountId = accountId;
        this.status = status;
    }
}
