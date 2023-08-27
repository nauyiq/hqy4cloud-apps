package com.hqy.cloud.message.common.im.enums;

import java.util.Arrays;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 14:13
 */
public enum ImMessageType {

    /**
     * 系统消息
     */
    SYSTEM("system"),

    /**
     * 文本
     */
    TEXT("text"),

    /**
     * 文件
     */
    FILE("file"),

    /**
     * 语音
     */
    ViDEO("video")

    ;

    public final String type;

    ImMessageType(String type) {
        this.type = type;
    }

    public static boolean isEnabled(String type) {
        ImMessageType[] values = values();
        return Arrays.stream(values).anyMatch(e -> e.type.equals(type));
    }

}
