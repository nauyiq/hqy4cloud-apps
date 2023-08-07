package com.hqy.cloud.apps.blog.socket.listener;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.hqy.cloud.apps.blog.service.opeations.ChatgptOperationService;
import com.hqy.cloud.socketio.starter.core.support.EventListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/4 9:49
 */
@Slf4j
@Component
public class ChatgptMessageListener extends EventListener {
    public static final String NAME = "ChatgptMessage";

    public ChatgptMessageListener(ChatgptOperationService operationService) {
        super(NAME, String.class, new ChatgptMessageDataListener(operationService));
    }

    private record ChatgptMessageDataListener(
            ChatgptOperationService operationService) implements DataListener<ChatgptMessage> {
        @Override
        public void onData(SocketIOClient client, ChatgptMessage data, AckRequest ackSender) throws Exception {
            if (data == null || !data.isEnable()) {
                log.warn("Receive message is invalid.");
                return;
            }
            String bizId = client.getHandshakeData().getBizId();
            if (StringUtils.isBlank(bizId)) {
                log.error("SocketIoClient should not be found bizId.");
                return;
            }
            Long userId = Long.parseLong(bizId);
            operationService.streamChatCompletion(userId, data,  client);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ChatgptMessage {
        private String model;
        private String prompt;
        private String chatId;

        public boolean isEnable() {
            return StringUtils.isNotBlank(prompt);
        }


    }




}
