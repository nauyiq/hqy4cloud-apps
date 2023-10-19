package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.message.tk.entity.ImMessage;
import com.hqy.cloud.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_PRIVATE_TO_UNDO_MESSAGE_CONTENT;
import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_TO_UNDO_MESSAGE_CONTENT;

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
    private ImMessageDTO message;

    public static UndoMessageEvent of(List<String> users, String undoAccount, ImMessage message) {
        ImMessageDTO messageDTO = new ImMessageDTO(message);
        if (message.getGroup()) {
            messageDTO.setContent(undoAccount + IM_TO_UNDO_MESSAGE_CONTENT);
        } else {
            messageDTO.setContent(IM_PRIVATE_TO_UNDO_MESSAGE_CONTENT);
        }
        return new UndoMessageEvent(users, messageDTO);
    }

    @Override
    public String name() {
        return "undoMessage";
    }

    public String message() {
        return JsonUtil.toJson(message);
    }

    public boolean isGroup() {
        return message.getIsGroup();
    }
}
