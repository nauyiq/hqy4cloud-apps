package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {

    private Long id;
    private Long contactId;
    private String displayName;
    private String avatar;
    private Integer unread;
    private Boolean isGroup;
    private Boolean isNotice;
    private Boolean isTop;
    private String notice;
    private Integer role;
    private Long creator;
    private Integer lastMessageType;
    private Long lastMessageTime;
    private String lastMessageContent;
    private Long lastReadTime;
    private Date lastReadDate;

}
