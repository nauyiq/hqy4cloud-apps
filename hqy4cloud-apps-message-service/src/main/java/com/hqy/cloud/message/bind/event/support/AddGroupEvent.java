package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.event.ImEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增群聊事件
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/16 14:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddGroupEvent implements ImEvent {

    /**
     * id
     */
    private String id;

    /**
     * 展示名
     */
    private String displayName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否是群
     */
    private Boolean isGroup;

    /**
     * 是否消息提醒
     */
    private Boolean isNotice;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 未读消息数
     */
    private Integer unread;

    /**
     * 群聊用户角色
     */
    private Integer role;

    /**
     * 群聊邀请确认
     */
    private Boolean invite;

    /**
     * 群公告
     */
    private String notice;

    /**
     * 群聊创建者
     */
    private String creator;

    /**
     * 最后一条消息类型
     */
    private String type;

    /**
     * 最近一条消息的时间戳，13位毫秒
     */
    private Long lastSendTime;

    /**
     * 最近一条消息的内容
     */
    private String lastContent;


    @Override
    public String name() {
        return "AddGroupEvent";
    }
}
