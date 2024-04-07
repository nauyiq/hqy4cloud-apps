package com.hqy.cloud.message.bind.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 聊天消息内容枚举类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/1
 */
public enum MessageType {

    /**
     * 文本消息
     */
    TEXT(1, "text"),

    /**
     * 图片消息
     */
    IMAGE(2, "image"),

    /**
     * 文件消息
     */
    FILE(3, "file"),

    /**
     * 语音消息
     */
    VIDEO(5, "video"),




    ;

    public final Integer type;
    public final String name;

    MessageType(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * 允许客户端发送的消息类型名字集合
     */
    public static final List<String> SUPPORT_CLIENT_MESSAGES = List.of(TEXT.name, IMAGE.name, FILE.name);

    /**
     * 文件消息集合
     */
    public static final List<String> FILE_MESSAGES = List.of(IMAGE.name, FILE.name, VIDEO.name);
    public static final List<Integer> FILE_TYPE_MESSAGES = List.of(IMAGE.type, FILE.type, VIDEO.type);


    public static boolean isEnabled(String type) {
        return SUPPORT_CLIENT_MESSAGES.contains(type);
    }

    public static boolean isFileMessage(String type) {
        return FILE_MESSAGES.contains(type);
    }

    public static boolean isFileMessage(Integer type) {
        return FILE_TYPE_MESSAGES.contains(type);
    }


    public static MessageType getMessageType(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }
        MessageType[] values = values();
        for (MessageType messageType : values) {
            String name = messageType.name;
            if (name.trim().equalsIgnoreCase(type.trim())) {
                return messageType;
            }
        }
        return null;
    }


    public static String getMessageType(Integer type) {
        if (type == null) {
            return null;
        }
        MessageType[] values = values();
        for (MessageType messageType : values) {
            if (messageType.type.equals(type)) {
                return messageType.name;
            }
        }

        EventMessageType[] types = EventMessageType.values();
        for (EventMessageType messageType : types) {
            if (messageType.type.equals(type)) {
                return messageType.contentType;
            }
        }
        return TEXT.name;
    }

    public static boolean enabledForwardOrSearchEs(Integer type) {
        return type.equals(TEXT.type) || type.equals(IMAGE.type) || type.equals(FILE.type);
    }





}
