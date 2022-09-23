package com.hqy.message.service;

import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.socketio.service.ThriftSocketIoPushService;

/**
 * 消息服务： 对外提供的rpc 接口发送socket.io事件
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/24 17:18
 */
@ThriftService(MicroServiceConstants.MESSAGE_NETTY_SERVICE)
public interface SocketIoMessagePushService extends ThriftSocketIoPushService {

}
