package com.hqy.blog.vo;

import lombok.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10 16:14
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class StatisticsVO {

    private Integer visits;
    private Integer likes;
    private Integer comments;

}
