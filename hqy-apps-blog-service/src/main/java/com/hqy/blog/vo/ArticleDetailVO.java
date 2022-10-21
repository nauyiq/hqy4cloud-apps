package com.hqy.blog.vo;

import cn.hutool.core.date.DateUtil;
import com.hqy.blog.dto.AccountAccessArticleStatusDTO;
import com.hqy.blog.dto.StatisticsDTO;
import com.hqy.blog.entity.Article;
import lombok.*;

/**
 * ArticleDetailVO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/13 10:11
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class ArticleDetailVO {

    private String id;
    private String title;
    private String content;
    private String backgroundMusic;
    private String backgroundMusicName;
    private String author;
    private String description;
    private String cover;
    private Integer type;
    private String created;
    private StatisticsVO statistics;
    private AccountAccessArticleStatusDTO accessStatus;


    public ArticleDetailVO(String authorName, Article article, StatisticsDTO statistics, AccountAccessArticleStatusDTO accessStatus) {
        this.id = article.getId().toString();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.backgroundMusic = article.getBackgroundMusic();
        this.backgroundMusicName = article.getBackgroundMusicName();
        this.author = authorName;
        this.description = article.getDescription();
        this.cover = article.getCover();
        this.type = article.getType();
        this.created = DateUtil.date(article.getCreated()).toString();
        this.statistics = new StatisticsVO(statistics.getVisits(), statistics.getLikes(), statistics.getComments());
        this.accessStatus = accessStatus;
    }
}
