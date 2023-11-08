package com.hqy.cloud.apps.commom.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/22 13:20
 */
@RequiredArgsConstructor
public enum UserBlogState {

    /**
     * 未读和未点赞
     */
    NONE(0),

    /**
     * 只访问了文章
     */
    ONLY_READ(1),

    /**
     * 只点赞了文章
     */
    ONLY_LIKE(2),

    /**
     * 点赞和阅读
     */
    READ_LIKED(3),



    ;
    @Getter
    private final int state;




}
