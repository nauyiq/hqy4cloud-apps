package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.event.ImEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/24 14:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivateChatEvent implements ImEvent {
    private ImMessageDTO messageDTO;

    @Override
    public String name() {
        return "privateChat";
    }


}
