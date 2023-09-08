package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/4 13:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImNoticeChatEvent implements ImEvent {

    private String to;
    private Payload payload;

    @Override
    public String name() {
        return "setChatNotice";
    }

    public String message() {
        return JsonUtil.toJson(payload);
    }

    public static ImNoticeChatEvent of(String to, String id, String conversationId, Boolean isNotice) {
        return new ImNoticeChatEvent(to, new ImNoticeChatEvent.Payload(id, conversationId, isNotice));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static final class Payload {
        private String id;
        private String conversationId;
        private Boolean isNotice;
    }
}
