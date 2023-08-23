package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 16:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {

    /**
     * 主键id
     */
    private String id;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 是否是群聊消息
     */
    private Boolean isGroup;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 发送用户信息
     */
    private UserInfoVO fromUser;


    /**
     * 联系人/群 id
     */
    private String toContactId;

    /**
     * 消息状态
     */
    private String status;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息发送时间
     */
    private Long sendTime;













}
