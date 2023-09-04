package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.message.bind.vo.ContactVO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/4 13:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppendChatEvent implements ImEvent {

    private boolean isGroup;
    private List<String> users;
    private Payload payload;

    public static AppendChatEvent of(boolean isGroup, List<String> users, ConversationVO conversation, ContactVO contact) {
        return new AppendChatEvent(isGroup, users, new Payload(conversation, contact));
    }

    public String message() {
        return JsonUtil.toJson(payload);
    }

    @Override
    public String name() {
        return "appendChat";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static final class Payload {
        private ConversationVO conversation;
        private ContactVO contact;
    }



}
