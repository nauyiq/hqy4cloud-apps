package com.hqy.cloud.apps.blog.entity;

import com.hqy.cloud.tk.model.BaseEntity;
import lombok.*;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 9:24
 */
@Data
@Table(name = "t_statistics")
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Statistics extends BaseEntity<Long> {

    /**
     * 访问数
     */
    private Integer visits;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 评论数
     */
    private Integer comments;



}
