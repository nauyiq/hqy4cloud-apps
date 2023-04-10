package com.hqy.cloud.apps.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 15:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTypeVO {

    private Integer id;
    private Integer code;
    private String status;
    private String name;
    private String created;



}
