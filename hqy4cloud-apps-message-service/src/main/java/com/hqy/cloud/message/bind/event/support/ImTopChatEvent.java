package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/4 11:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImTopChatEvent implements ImEvent {

    private String to;
    private Payload payload;

    @Override
    public String name() {
        return "setChatTop";
    }

    public String message() {
        return JsonUtil.toJson(payload);
    }

    public static ImTopChatEvent of(String to, String id, String conversationId, Boolean isTop) {
        return new ImTopChatEvent(to, new Payload(id, conversationId, isTop));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static final class Payload {
        private String id;
        private String conversationId;
        private Boolean isTop;
    }


}
