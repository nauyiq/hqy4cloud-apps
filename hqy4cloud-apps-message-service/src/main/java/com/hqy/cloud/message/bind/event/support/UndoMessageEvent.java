package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private ImMessageDTO message;

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
