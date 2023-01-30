package com.hqy.blog.dto;

import lombok.*;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10 16:19
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class PageArticleDTO {

    private Long id;
    private String title;
    private String description;
    private String cover;
    private Integer type;
    private Date created;
    private Boolean status;

}
