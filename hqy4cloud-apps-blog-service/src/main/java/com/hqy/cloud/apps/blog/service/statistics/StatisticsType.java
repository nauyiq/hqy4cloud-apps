package com.hqy.cloud.apps.blog.service.statistics;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 10:43
 */
public enum StatisticsType {

    /**
     * 访问数
     */
    VISITS(0),

    /**
     * 点赞数
     */
    LIKES(1),

    /**
     * 评论数
     */
    COMMENTS(2),

    ;

    public int type;

    public void setType(int type) {
        this.type = type;
    }


    StatisticsType(int type) {
        this.type = type;
    }
}
