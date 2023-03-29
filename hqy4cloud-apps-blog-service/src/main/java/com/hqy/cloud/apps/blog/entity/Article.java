package com.hqy.cloud.apps.blog.entity;

import com.google.common.base.Objects;
import com.hqy.cloud.tk.model.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * entity for t_blog.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:16
 */
@Data
@NoArgsConstructor
@Table(name = "t_article")
public class Article extends BaseEntity<Long> {

    /**
     * 标题
     */
    private String title;

    /**
     * 简介
     */
    private String intro;

    /**
     * 封面
     */
    private String cover;

    /**
     * 内容
     */
    private String content;

    /**
     * 类型
     */
    @Column(name = "'type'")
    private Integer type;

    /**
     * 背景音乐
     */
    private String backgroundMusic;

    /**
     * 背景音乐名
     */
    private String backgroundMusicName;

    /**
     * 作者
     */
    private Long author;

    /**
     * 状态
     */
    @Column(name = "'status'")
    private Boolean status;

    /**
     * 是否删除
     */
    private Boolean deleted;

    public Article(Integer type) {
        this.type = type;
    }

    public Article(Long id, String title, String intro, String cover, String content, Integer type, String backgroundMusic, String backgroundMusicName, Long author, Boolean status, Date date) {
        super(id, date);
        this.title = title;
        this.intro = intro;
        this.cover = cover;
        this.content = content;
        this.type = type;
        this.backgroundMusic = backgroundMusic;
        this.backgroundMusicName = backgroundMusicName;
        this.author = author;
        this.status = status;
        this.deleted = false;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("title", title)
                .append("description", intro)
                .append("cover", cover)
                .append("content", content)
                .append("type", type)
                .append("backgroundMusic", backgroundMusic)
                .append("backgroundMusicName", backgroundMusicName)
                .append("author", author)
                .append("status", status)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Article article = (Article) o;
        return Objects.equal(title, article.title) && Objects.equal(intro, article.intro) && Objects.equal(cover, article.cover) && Objects.equal(content, article.content) && Objects.equal(type, article.type) && Objects.equal(backgroundMusic, article.backgroundMusic) && Objects.equal(backgroundMusicName, article.backgroundMusicName) && Objects.equal(author, article.author) && Objects.equal(status, article.status);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), title, intro, cover, content, type, backgroundMusic, backgroundMusicName, author, status);
    }
}
