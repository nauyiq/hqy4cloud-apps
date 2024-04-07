package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
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
    private ImMessageVO message;

    @Override
    public String name() {
        return "privateChat";
    }


}
