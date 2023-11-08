package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 14:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationVO {

    /**
     * id
     */
    private String id;

    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 展示用户名
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
     * 是否移除聊天
     */
    private Boolean isRemove;

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
     * 群创建者名字
     */
    private String creatorName;

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



}
