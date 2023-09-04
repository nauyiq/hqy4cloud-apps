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
import org.springframework.stereotype.Service;

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
@Service
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
        try {
            SocketClusterStatus query = SocketClusterStatusManager.query(Environment.getInstance().getEnvironment(), MicroServiceConstants.MESSAGE_NETTY_SERVICE);
            Set<String> ids = events.parallelStream().map(AddGroupEvent::getId).collect(Collectors.toSet());
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
        try {
            ThriftSocketIoPushService socketIoPushService = SocketIoConnectionUtil.getSocketIoPushService(to, ThriftSocketIoPushService.class, MicroServiceConstants.MESSAGE_NETTY_SERVICE);
            socketIoPushService.asyncPush(to, event.name(), JsonUtil.toJson(messageDTO));
            return true;
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }
    }

    @Override
    public boolean onGroupChat(GroupChatEvent event) {
        try {
            SocketClusterStatus query = SocketClusterStatusManager.query(Environment.getInstance().getEnvironment(), MicroServiceConstants.MESSAGE_NETTY_SERVICE);
            Set<String> ids = event.getIds();
            String message = JsonUtil.toJson(event.getMessage());
            if (query.isEnableMultiWsNode()) {
                Map<String, ThriftSocketIoPushService> map = SocketIoConnectionUtil.getMultipleSocketIoPushService(ids, ThriftSocketIoPushService.class, MicroServiceConstants.MESSAGE_NETTY_SERVICE);
                if (MapUtil.isNotEmpty(map)) {
                    ids.forEach(id -> map.get(id).asyncPush(id, event.name(), message));
                    return true;
                }
            }
            ids.forEach(id -> socketIoPushService.asyncPush(id, event.name(), message));
            return true;
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }
    }

    @Override
    public boolean onReadMessages(ReadMessagesEvent event) {
        try {
            ThriftSocketIoPushService socketIoPushService = SocketIoConnectionUtil.getSocketIoPushService(event.getTo(), ThriftSocketIoPushService.class, MicroServiceConstants.MESSAGE_NETTY_SERVICE);
            socketIoPushService.asyncPush(event.getTo(), event.name(), JsonUtil.toJson(event.getMessages()));
            return true;
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }
    }

    @Override
    public boolean onImTopChatEvent(ImTopChatEvent event) {
        try {
            ThriftSocketIoPushService socketIoPushService = SocketIoConnectionUtil.getSocketIoPushService(event.getTo(), ThriftSocketIoPushService.class, MicroServiceConstants.MESSAGE_NETTY_SERVICE);
            socketIoPushService.asyncPush(event.getTo(), event.name(), event.message());
            return true;
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }
    }

    @Override
    public boolean onImNoticeChatEvent(ImNoticeChatEvent event) {
        try {
            ThriftSocketIoPushService socketIoPushService = SocketIoConnectionUtil.getSocketIoPushService(event.getTo(), ThriftSocketIoPushService.class, MicroServiceConstants.MESSAGE_NETTY_SERVICE);
            socketIoPushService.asyncPush(event.getTo(), event.name(), event.message());
            return true;
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }
    }

    @Override
    public boolean onImAppendChatEvent(AppendChatEvent event) {
        try {
            String message = event.message();
            if (!event.isGroup()) {
                // private conversation chat.
                String to = event.getUsers().get(0);
                ThriftSocketIoPushService socketIoPushService = SocketIoConnectionUtil.getSocketIoPushService(to, ThriftSocketIoPushService.class, MicroServiceConstants.MESSAGE_NETTY_SERVICE);
                socketIoPushService.asyncPush(to, event.name(), message);
            } else {
                Set<String> ids = new HashSet<>(event.getUsers());
                SocketClusterStatus query = SocketClusterStatusManager.query(Environment.getInstance().getEnvironment(), MicroServiceConstants.MESSAGE_NETTY_SERVICE);
                if (query.isEnableMultiWsNode()) {
                    Map<String, ThriftSocketIoPushService> pushServiceMap = SocketIoConnectionUtil.getMultipleSocketIoPushService(ids, ThriftSocketIoPushService.class, MicroServiceConstants.MESSAGE_NETTY_SERVICE);
                    if (MapUtil.isNotEmpty(pushServiceMap)) {
                        ids.forEach(id -> pushServiceMap.get(id).asyncPush(id, event.name(), message));
                        return true;
                    }
                }
                socketIoPushService.asyncPushMultiple(ids, event.name(), message);
            }
            return true;
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }
    }

    @Override
    public boolean onImUndoMessageEvent(UndoMessageEvent event) {
        return false;
    }





}
