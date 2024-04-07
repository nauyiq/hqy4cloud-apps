package com.hqy.cloud.message.common.im.enums;

import java.util.Arrays;
import java.util.List;

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
     * 事件消息
     */
    EVENT("event"),

    /**
     * 图片消息
     */
    IMAGE("image"),

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

    public static final List<String> FILES_TYPES = List.of(IMAGE.type, FILE.type, ViDEO.type);

    ImMessageType(String type) {
        this.type = type;
    }

    public static boolean isEnabled(String type) {
        ImMessageType[] values = values();
        return Arrays.stream(values).anyMatch(e -> e.type.equals(type));
    }

    public static boolean isFileType(String type) {
        return FILES_TYPES.contains(type);
    }

}
