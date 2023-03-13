package com.hqy.cloud.apps.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 11:00
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AdminPageCommentsVO {

    private String id;
    private String articleId;
    private String content;
    private String commentName;
    private String replyName;
    private Integer level;
    private String created;
    private Boolean deleted;

}
