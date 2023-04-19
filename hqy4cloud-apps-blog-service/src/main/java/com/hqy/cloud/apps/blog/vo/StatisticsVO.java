package com.hqy.cloud.apps.blog.vo;

import com.hqy.cloud.apps.blog.dto.StatisticsDTO;
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

    public StatisticsVO(StatisticsDTO statistics){
        this.visits = statistics.getVisits();
        this.likes = statistics.getLikes();
        this.comments = statistics.getComments();
    }

}
