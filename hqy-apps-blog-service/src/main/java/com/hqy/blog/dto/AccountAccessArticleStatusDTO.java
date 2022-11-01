package com.hqy.blog.dto;

import com.hqy.blog.statistics.StatisticsType;
import lombok.*;

/**
 * 用户访问文章状态 DTO.
 * AccountAccessArticleStatusDTO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/12 18:07
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class AccountAccessArticleStatusDTO {

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 是否点赞
     */
    private Boolean isLike;


    public boolean getStatus(StatisticsType type) {
        if (type == null) {
            return false;
        }
        if (type == StatisticsType.LIKES) {
            return isLike;
        } else {
            return isRead;
        }
    }



}
