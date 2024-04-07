package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 16:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImMessageVO {

    /**
     * 消息id
     */
    private String id;

    /**
     * 表 im_message 主键id
     */
    private String messageId;

    /**
     * 是否是群聊消息
     */
    private Boolean isGroup;

    /**
     * 已读未读
     */
    private Boolean isRead;

    /**
     * 发送用户信息
     */
    private UserInfoVO fromUser;

    /**
     * 接收人用户信息
     */
    private UserInfoVO contactUser;


    /**
     * 联系人/群 id
     */
    @NotEmpty
    private String toContactId;

    /**
     * 内容
     */
    @NotEmpty
    private String content;

    /**
     * 消息状态
     */
    private String status;

    /**
     * 消息类型
     */
    @NotEmpty
    private String type;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件名
     */
    private String fileName;


    /**
     * 消息发送时间
     */
    private Long sendTime;


















}
