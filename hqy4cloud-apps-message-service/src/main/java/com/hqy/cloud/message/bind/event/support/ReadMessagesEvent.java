package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.event.ImEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/25 16:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadMessagesEvent implements ImEvent {

    private String to;
    private List<String> messages;

    @Override
    public String name() {
        return "readMessages";
    }
}
