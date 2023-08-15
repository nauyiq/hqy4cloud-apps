package com.hqy.cloud.message.server.support;

import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.socketio.event.ContactOnlineOfflineEvent;
import com.hqy.cloud.message.tk.entity.ImContact;
import com.hqy.cloud.message.tk.service.ImContactTkService;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.rpc.thrift.service.ThriftSocketIoPushService;
import com.hqy.cloud.socketio.starter.core.support.SocketIoConnectionUtil;
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
    private final ImContactTkService contactTkService;
    private final ThriftSocketIoPushService socketIoPushService;

    @Override
    public boolean doContactOnlineOffline(ContactOnlineOfflineEvent event) {
        if (event == null) {
            log.warn("ContactOnlineOfflineEvent is should not be null.");
            return false;
        }
        Long id = event.id();
        List<ImContact> contacts = contactTkService.queryList(ImContact.of(id, false));
        if (CollectionUtils.isEmpty(contacts)) {
            return true;
        }
        Set<String> userIds = contacts.parallelStream().map(m -> m.getUserId().toString()).collect(Collectors.toSet());
        SocketClusterStatus query = SocketClusterStatusManager.query(Environment.getInstance().getEnvironment(), MicroServiceConstants.MESSAGE_NETTY_SERVICE);
        if (!query.isEnableMultiWsNode()) {
            socketIoPushService.asyncPushMultiple(userIds, ContactOnlineOfflineEvent.EVENT, JsonUtil.toJson(event));
            return true;
        }

        //集群模式下需要通过rpc进行转发
        ThriftSocketIoPushService pushService;
        for (String userId : userIds) {
            try {
                pushService = SocketIoConnectionUtil.getSocketIoPushService(userId, ThriftSocketIoPushService.class, MicroServiceConstants.MESSAGE_NETTY_SERVICE);
                pushService.asyncPush(userId, ContactOnlineOfflineEvent.EVENT, JsonUtil.toJson(event));
            } catch (Throwable cause) {
                log.error(cause.getMessage(), cause);
            }
        }
        return true;
    }
}
