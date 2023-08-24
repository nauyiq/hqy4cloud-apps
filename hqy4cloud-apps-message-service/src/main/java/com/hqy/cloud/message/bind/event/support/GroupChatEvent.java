package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.event.ImEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/24 14:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatEvent implements ImEvent {
    private Set<String> ids;
    private ImMessageDTO message;

    @Override
    public String name() {
        return "groupChat";
    }
}
