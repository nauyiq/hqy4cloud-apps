package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/4 17:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UndoMessageEvent implements ImEvent {
    private List<String> users;
    private Payload payload;

    public static UndoMessageEvent of(List<String> users, boolean isGroup, Long contactId, Long id, String messageId, String content, Date created) {
        ImMessageDTO messageDTO = new ImMessageDTO();
        messageDTO.setId(messageId);
        messageDTO.setMessageId(id.toString());
        messageDTO.setContent(content);
        messageDTO.setIsGroup(isGroup);
        messageDTO.setSendTime(created.getTime());
        Payload payload = new Payload(contactId.toString(), messageDTO);
        return new UndoMessageEvent(users, payload);
    }

    @Override
    public String name() {
        return "undoMessage";
    }

    public String message() {
        return JsonUtil.toJson(payload);
    }

    public boolean isGroup() {
        return payload.message.getIsGroup();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Payload {
        private String contactId;
        private ImMessageDTO message;
    }

}
