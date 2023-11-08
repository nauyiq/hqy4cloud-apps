package com.hqy.cloud.apps.blog.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/22 11:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_article_user_state")
@EqualsAndHashCode(callSuper = true)
public class ArticleUserState extends BaseEntity<Long> {

    private Long articleId;
    private Long accountId;
    private int state;

    public ArticleUserState(Long id, Long articleId, Long accountId, int state) {
        super.setId(id);
        this.articleId = articleId;
        this.accountId = accountId;
        this.state = state;
    }
}
