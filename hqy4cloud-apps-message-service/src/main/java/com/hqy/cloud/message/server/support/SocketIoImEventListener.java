package com.hqy.cloud.message.server.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.event.support.*;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.rpc.thrift.service.ThriftSocketIoPushService;
import com.hqy.cloud.socketio.starter.core.support.SocketIoConnectionUtil;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 16:03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SocketIoImEventListener implements ImEventListener {
    private final ImConversationTkService contactTkService;
    private final ThriftSocketIoPushService socketIoPushService;

    @Override
    public boolean onContactOnlineOffline(ContactOnlineOfflineEvent event) {
        AssertUtil.notNull(event, "ContactOnlineOfflineEvent is should not be null.");
        Long id = event.id();
        List<ImConversation> contacts = contactTkService.queryList(ImConversation.of(id, false));
        if (CollectionUtils.isEmpty(contacts)) {
            return true;
        }
        Set<String> userIds = contacts.parallelStream().map(m -> m.getUserId().toString()).collect(Collectors.toSet());
        SocketClusterStatus query = SocketClusterStatusManager.query(Environment.getInstance().getEnvironment(), MicroServiceConstants.MESSAGE_NETTY_SERVICE);
        if (!query.isEnableMultiWsNode()) {
            socketIoPushService.asyncPushMultiple(userIds, event.name(), JsonUtil.toJson(event));
            return true;
        }
        //集群模式下需要通过rpc进行转发
        ThriftSocketIoPushService pushService;
        for (String userId : userIds) {
            try {
                pushService = SocketIoConnectionUtil.getSocketIoPushService(userId, ThriftSocketIoPushService.class, MicroServiceConstants.MESSAGE_NETTY_SERVICE);
                pushService.asyncPush(userId, event.name(), JsonUtil.toJson(event));
            } catch (Throwable cause) {
                log.error(cause.getMessage(), cause);
            }
        }
        return true;
    }

    @Override
    public boolean onAddGroup(List<AddGroupEvent> events) {
        Set<String> ids = events.parallelStream().map(AddGroupEvent::getId).collect(Collectors.toSet());
        try {
            SocketClusterStatus query = SocketClusterStatusManager.query(Environment.getInstance().getEnvironment(), MicroServiceConstants.MESSAGE_NETTY_SERVICE);

            if (query.isEnableMultiWsNode()) {
                Map<String, ThriftSocketIoPushService> map = SocketIoConnectionUtil.getMultipleSocketIoPushService(ids, ThriftSocketIoPushService.class, MicroServiceConstants.MESSAGE_NETTY_SERVICE);
                if (MapUtil.isNotEmpty(map)) {
                    events.forEach(event -> map.getOrDefault(event.getId(), socketIoPushService).asyncPush(event.getId(), event.name(), JsonUtil.toJson(event)));
                    return true;
                }
            }
            events.forEach(event -> socketIoPushService.asyncPush(event.getId(), event.name(), JsonUtil.toJson(event)));
            return true;
        } catch (Throwable cause) {
            log.warn(cause.getMessage(), cause);
            return false;
        }
    }

    @Override
    public boolean onPrivateChat(PrivateChatEvent event) {
        ImMessageDTO messageDTO = event.getMessageDTO();
        String to = messageDTO.getToContactId();
        return SocketIoConnectionUtil.doPrivateMessage(true, MicroServiceConstants.MESSAGE_NETTY_SERVICE, to, event.name(), JsonUtil.toJson(messageDTO));
    }

    @Override
    public boolean onGroupChat(GroupChatEvent event) {
        Set<String> ids = event.getIds();
        String message = JsonUtil.toJson(event.getMessage());
        return SocketIoConnectionUtil.doBroadcastMessages(true, MicroServiceConstants.MESSAGE_NETTY_SERVICE, ids, event.name(), message);
    }

    @Override
    public boolean onReadMessages(ReadMessagesEvent event) {
        String messagePayload = JsonUtil.toJson(event.getMessages());
        return SocketIoConnectionUtil.doPrivateMessage(true, MicroServiceConstants.MESSAGE_NETTY_SERVICE, event.getTo(), event.name(), messagePayload);
    }

    @Override
    public boolean onImTopChatEvent(ImTopChatEvent event) {
        return SocketIoConnectionUtil.doPrivateMessage(true, MicroServiceConstants.MESSAGE_NETTY_SERVICE, event.getTo(), event.name(), event.message());
    }

    @Override
    public boolean onImNoticeChatEvent(ImNoticeChatEvent event) {
        return SocketIoConnectionUtil.doPrivateMessage(true, MicroServiceConstants.MESSAGE_NETTY_SERVICE, event.getTo(), event.name(), event.message());
    }

    @Override
    public boolean onImAppendChatEvent(AppendChatEvent event) {
        String message = event.message();
        if (!event.isGroup()) {
            // private conversation chat.
            String to = event.getUsers().get(0);
            return SocketIoConnectionUtil.doPrivateMessage(true, MicroServiceConstants.MESSAGE_NETTY_SERVICE, to, event.name(), message);
        } else {
            Set<String> ids = new HashSet<>(event.getUsers());
            return SocketIoConnectionUtil.doBroadcastMessages(true, MicroServiceConstants.MESSAGE_NETTY_SERVICE, ids, event.name(), message);
        }
    }

    @Override
    public boolean onImUndoMessageEvent(UndoMessageEvent event) {
        String message = event.message();
        if (event.isGroup()) {
            Set<String> ids = new HashSet<>(event.getUsers());
            return SocketIoConnectionUtil.doBroadcastMessages(true, MicroServiceConstants.MESSAGE_NETTY_SERVICE, ids, event.name(), message);
        } else {
            // private conversation chat.
            String to = event.getUsers().get(0);
            return SocketIoConnectionUtil.doPrivateMessage(true, MicroServiceConstants.MESSAGE_NETTY_SERVICE, to, event.name(), message);
        }
    }

    @Override
    public boolean onContactNameChangeEvent(ContactNameChangeEvent event) {
        String message = event.messagePayload();
        if (event.isGroup()) {
            Set<String> ids = new HashSet<>(event.getUsers());
            return SocketIoConnectionUtil.doBroadcastMessages(true, MicroServiceConstants.MESSAGE_NETTY_SERVICE, ids, event.name(), message);
        } else {
            // private conversation chat.
            String to = event.getUsers().get(0);
            return SocketIoConnectionUtil.doPrivateMessage(true, MicroServiceConstants.MESSAGE_NETTY_SERVICE, to, event.name(), message);
        }
    }
}
