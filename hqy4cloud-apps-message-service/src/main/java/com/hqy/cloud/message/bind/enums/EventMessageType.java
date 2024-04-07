package com.hqy.cloud.message.bind.enums;

import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 事件消息类型, 用于特殊场景的系统消息
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/12
 */
public enum EventMessageType {


    /**
     * 添加好友消息
     */
    FRIEND(9, "text"),

    /**
     * 添加好友提示
     */
    ADD_FRIEND_NOTICE(10, "event"),

    /**
     * 消息撤回
     */
    UNDO(11, "event"),

    /**
     * 创建群聊
     */
    EVENT_CREATE_GROUP(12, "event", "im.group.created"),

    /**
     * 群名表更
     */
    EVENT_GROUP_NAME_MODIFIED(13, "event", "im.group.name.modified"),

    /**
     * 群通知变更
     */
    EVENT_GROUP_NOTICE_MODIFIED(14, "event", "im.group.notice.modified"),

    /**
     * 群成员移除事件
     */
    EVENT_GROUP_MEMBER_REMOVED(15, "event", "im.group.member.removed"),

    /**
     * 群解散事件
     */
    EVENT_GROUP_REMOVED(16, "event", "im.group.removed"),

    ;

    /**
     * 入库的type值
     */
    public final Integer type;


    /**
     * 聊天内容类型
     */
    public final String contentType;

    /**
     * 对应的语言翻译key
     */
    public final String translateKey;

    private static final List<Integer> EVENT_TYPES = Arrays.stream(EventMessageType.values()).map(type -> type.type).toList();

    EventMessageType(Integer type, String contentType) {
        this.type = type;
        this.contentType = contentType;
        this.translateKey = StrUtil.EMPTY;
    }

    EventMessageType(Integer type, String contentType, String translateKey) {
        this.type = type;
        this.contentType = contentType;
        this.translateKey = translateKey;
    }

    public static boolean isEventType(Integer type) {
        return EVENT_TYPES.contains(type);
    }

    public static EventMessageType getType(Integer type) {
        EventMessageType[] values = values();
        for (EventMessageType messageType : values) {
            if (messageType.type.equals(type)) {
                return messageType;
            }
        }
        return null;
    }


}
