package com.hqy.cloud.message.server.support;

import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.socketio.event.AddGroupEvent;
import com.hqy.cloud.message.socketio.event.ContactOnlineOfflineEvent;
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

import java.util.List;
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
    public boolean doContactOnlineOffline(ContactOnlineOfflineEvent event) {
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
    public boolean doAddGroup(List<AddGroupEvent> events) {
        SocketClusterStatus query = SocketClusterStatusManager.query(Environment.getInstance().getEnvironment(), MicroServiceConstants.MESSAGE_NETTY_SERVICE);
        for (AddGroupEvent event : events) {
            try {
                if (!query.isEnableMultiWsNode()) {
                    socketIoPushService.asyncPush(event.getId(), event.name(), JsonUtil.toJson(event));
                } else {
                    ThriftSocketIoPushService service = SocketIoConnectionUtil.getSocketIoPushService(event.getId(), ThriftSocketIoPushService.class, MicroServiceConstants.MESSAGE_NETTY_SERVICE);
                    service.asyncPush(event.getId(), event.name(), JsonUtil.toJson(event));
                }
            } catch (Throwable cause) {
                log.error(cause.getMessage());
            }
        }
        return true;
    }
}
