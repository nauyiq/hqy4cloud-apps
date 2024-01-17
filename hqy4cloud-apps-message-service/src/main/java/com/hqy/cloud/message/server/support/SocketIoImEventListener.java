package com.hqy.cloud.message.server.support;

import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.event.support.*;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.service.SocketIoMessagePushService;
import com.hqy.cloud.netty.socketio.thrift.SocketIoThriftDiscovery;
import com.hqy.cloud.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SocketIoImEventListener implements ImEventListener {
    private final SocketIoThriftDiscovery socketIoThriftDiscovery;

    @Override
    public boolean onPrivateChat(PrivateChatEvent event) {
        ImMessageDTO messageDTO = event.getMessageDTO();
        String to = messageDTO.getToContactId();
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, to, event.name(), JsonUtil.toJson(messageDTO),
                SocketIoMessagePushService.class, true);
    }

    @Override
    public boolean onGroupChat(GroupChatEvent event) {
        Set<String> ids = event.getIds();
        String message = JsonUtil.toJson(event.getMessage());
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, ids, event.name(), JsonUtil.toJson(message),
                SocketIoMessagePushService.class, true);
    }

    @Override
    public boolean onReadMessages(ReadMessagesEvent event) {
        String messagePayload = JsonUtil.toJson(event.getMessages());
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, event.getTo(), event.name(), messagePayload,
                SocketIoMessagePushService.class, true);
    }

    @Override
    public boolean onImTopChatEvent(ImTopChatEvent event) {
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, event.getTo(), event.name(),
                event.message(), SocketIoMessagePushService.class,  true);
    }

    @Override
    public boolean onImNoticeChatEvent(ImNoticeChatEvent event) {
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, event.getTo(), event.name(),
                event.message(), SocketIoMessagePushService.class,  true);
    }

    @Override
    public boolean onImAppendPrivateChatEvent(AppendChatEvent event) {
        String message = event.message();
        // private conversation chat.
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, event.getUser(), event.name(),
                message, SocketIoMessagePushService.class,  true);
    }

    @Override
    public boolean onImAppendGroupChatEvent(List<AppendChatEvent> events) {
        Map<String, String> messagesMap = events.parallelStream().collect(Collectors.toMap(AppendChatEvent::getUser, AppendChatEvent::message));
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, messagesMap, events.get(0).name(), SocketIoMessagePushService.class,true);
    }

    @Override
    public boolean onAddFriendApplicationEvent(FriendApplicationEvent event) {
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, event.getTo(), event.name(),
                event.message(), SocketIoMessagePushService.class,  true);
    }

    @Override
    public boolean onImUndoMessageEvent(UndoMessageEvent event) {
        String message = event.message();
        if (event.isGroup()) {
            Set<String> ids = new HashSet<>(event.getUsers());
            return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, ids, event.name(), message,
                    SocketIoMessagePushService.class,  true);
        } else {
            // private conversation chat.
            String to = event.getUsers().get(0);
            return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, to, event.name(), message,
                    SocketIoMessagePushService.class, true);
        }
    }

    @Override
    public boolean onContactNameChangeEvent(ContactNameChangeEvent event) {
        String message = event.messagePayload();
        if (event.isGroup()) {
            Set<String> ids = new HashSet<>(event.getUsers());
            return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, ids, event.name(), message,
                    SocketIoMessagePushService.class,  true);
        } else {
            // private conversation chat.
            String to = event.getUsers().get(0);
            return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, to, event.name(), message,
                    SocketIoMessagePushService.class, true);
        }
    }

    @Override
    public boolean onGroupNoticeChangeEvent(GroupNoticeEvent event) {
        Set<String> ids = new HashSet<>(event.getUserIds());
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, ids, event.name(), event.message(),
                SocketIoMessagePushService.class,  true);
    }

    @Override
    public boolean onRemoveGroupMemberEvent(RemoveGroupMemberEvent event) {
        Set<String> ids = new HashSet<>(event.getUsers());
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, ids, event.name(), event.message(),
                SocketIoMessagePushService.class,  true);
    }

    @Override
    public boolean onExitGroupMemberEvent(ExitGroupEvent event) {
        Set<String> ids = new HashSet<>(event.getUsers());
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, ids, event.name(), event.message(),
                SocketIoMessagePushService.class,  true);
    }

    @Override
    public boolean onAddGroupMemberEvent(AddGroupMemberEvent event) {
        Set<String> ids = new HashSet<>(event.getUsers());
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, ids, event.name(), event.message(),
                SocketIoMessagePushService.class,  true);
    }

    @Override
    public boolean onDeleteGroupEvent(DeleteGroupEvent event) {
        Set<String> ids = new HashSet<>(event.getUsers());
        return socketIoThriftDiscovery.pushEvent(MicroServiceConstants.MESSAGE_NETTY_SERVICE, ids, event.name(), event.message(),
                SocketIoMessagePushService.class,  true);
    }
}
