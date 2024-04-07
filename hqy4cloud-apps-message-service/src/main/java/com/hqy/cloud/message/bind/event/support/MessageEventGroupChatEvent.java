package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.event.ImEvent;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/14
 */
public record MessageEventGroupChatEvent(
        Map<Long, ImMessageDTO> messages) implements ImEvent {

    @Override
    public String name() {
        return "groupChat";
    }
}
