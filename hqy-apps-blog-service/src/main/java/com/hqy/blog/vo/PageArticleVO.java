package com.hqy.blog.vo;

import cn.hutool.core.date.DateUtil;
import com.hqy.blog.dto.PageArticleDTO;
import lombok.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10 16:07
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class PageArticleVO {

    private Long id;
    private String title;
    private String description;
    private String cover;
    private Integer type;
    private String created;
    private StatisticsVO statistics;


    public PageArticleVO(PageArticleDTO article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.description = article.getDescription();
        this.cover = article.getCover();
        this.type = article.getType();
        this.created = DateUtil.formatDateTime(article.getCreated());
    }
}
