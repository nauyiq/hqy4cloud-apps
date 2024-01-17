package com.hqy.cloud.message.service.impl;

import com.hqy.cloud.message.service.SocketIoMessagePushService;
import com.hqy.cloud.netty.socketio.SocketIoSocketServer;
import com.hqy.cloud.netty.socketio.thrift.AbstractThriftSocketIoPushService;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/24 17:20
 */
@Service
public class SocketIoMessagePushServiceImpl extends AbstractThriftSocketIoPushService implements SocketIoMessagePushService {

    protected SocketIoMessagePushServiceImpl(SocketIoSocketServer socketServer) {
        super(socketServer);
    }
}
