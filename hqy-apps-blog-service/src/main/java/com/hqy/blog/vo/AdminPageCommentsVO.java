package com.hqy.blog.vo;

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

    private Long id;
    private Long articleId;
    private String content;
    private String commentName;
    private String replyName;
    private Boolean status;
    private String created;

}
