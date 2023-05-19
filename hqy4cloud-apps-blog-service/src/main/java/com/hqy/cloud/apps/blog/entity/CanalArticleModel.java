package com.hqy.cloud.apps.blog.entity;

import com.hqy.cloud.canal.annotation.CanalModel;
import com.hqy.cloud.canal.common.FieldNamingPolicy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 9:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@CanalModel(database = "apps_blog", table = "t_article", fieldNamingPolicy = FieldNamingPolicy.LOWER_UNDERSCORE)
public class CanalArticleModel {
    private Long id;
    private String title;
    private String intro;
    private String cover;
    private String content;
    private Integer type;
    private String backgroundMusic;
    private String backgroundMusicName;
    private Long author;
    private Integer status;
    private Integer deleted;
    private Date created;
    private Date updated;

}
