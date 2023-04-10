package com.hqy.cloud.apps.blog.vo;

import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.apps.blog.dto.PageArticleDTO;
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

    private String id;
    private String title;
    private String description;
    private String cover;
    private Integer type;
    private String typeName;
    private String backgroundMusic;
    private String backgroundMusicName;
    private String content;
    private String status;
    private StatisticsVO statistics;
    private String created;


    public PageArticleVO(PageArticleDTO article) {
        this.id = article.getId().toString();
        this.title = article.getTitle();
        this.description = article.getDescription();
        this.cover = article.getCover();
        this.type = article.getType();
        this.created = DateUtil.formatDateTime(article.getCreated());
    }
}
