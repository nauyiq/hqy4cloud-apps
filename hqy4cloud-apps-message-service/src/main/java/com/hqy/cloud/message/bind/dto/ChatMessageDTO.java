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
public class ChatMessageDTO {

    private Long id;
    private String messageId;
    private Long send;
    private Long toContactId;
    private String content;
    private Integer type;
    private Integer status;
    private Boolean isRead = true;
    private Boolean isGroup;
    private Date created;


    public static ChatMessageDTO of(Long send, Long toContactId, String content, Integer type) {
        ChatMessageDTO messageDTO = new ChatMessageDTO();
        messageDTO.send = send;
        messageDTO.toContactId = toContactId;
        messageDTO.content = content;
        messageDTO.type = type;
        return messageDTO;
    }

    public static ChatMessageDTO of(String content, Integer type) {
        ChatMessageDTO messageDTO = new ChatMessageDTO();
        messageDTO.content = content;
        messageDTO.type = type;
        return messageDTO;
    }



}
