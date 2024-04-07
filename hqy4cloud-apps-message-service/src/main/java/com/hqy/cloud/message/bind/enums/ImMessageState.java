package com.hqy.cloud.message.bind.enums;

/**
 * 私聊消息状态枚举类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/1
 */
public enum ImMessageState {

    /**
     * 消息发送失败
     */
    FAILED(0, "failed"),

    /**
     * 消息发送成功
     */
    SUCCESS(1, "succeed"),

    /**
     * 消息被撤回
     */
    UNDO(2, "undo"),


    ;


    public final Integer value;
    public final String name;

    ImMessageState(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getState(Integer value) {
        for (ImMessageState state : values()) {
            if (state.value.equals(value)) {
                return state.name;
            }
        }
        return FAILED.name;
    }

}
