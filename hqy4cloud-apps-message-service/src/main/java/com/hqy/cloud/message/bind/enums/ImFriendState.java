package com.hqy.cloud.message.bind.enums;

/**
 * 好友状态枚举
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/1
 */
public enum ImFriendState {

    /**
     * 正常的好友关系
     */
    NORMAL(1),

    /**
     * 删除了对方，
     */
    REMOVE(2),

    /**
     * 对方删除了你
     */
    REMOVE_BY(3),

    ;


    public final Integer value;

    ImFriendState(Integer value) {
        this.value = value;
    }
}
