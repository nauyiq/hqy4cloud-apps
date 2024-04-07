package com.hqy.cloud.message.bind.enums;

/**
 * 好友申请状态枚举
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/7
 */
public enum ImFriendApplicationState {

    /**
     * 初始状态, 表示被申请添加好友的用户还未查看消息
     */
    UN_READ(0),

    /**
     * 已读，表示被申请的用户已经查看了消息
     */
    ALREADY_READ(1),

    /**
     * 已接受
     */
    ACCEPTED(2),

    /**
     * 已拒绝
     */
    REJECTED(3),

    /**
     * 请求超时
     */
    EXPIRED(4),





    ;

    public final Integer state;


    ImFriendApplicationState(Integer state) {
        this.state = state;
    }
}
