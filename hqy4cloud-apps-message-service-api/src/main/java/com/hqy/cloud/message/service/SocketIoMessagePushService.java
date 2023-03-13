package com.hqy.cloud.message.service;


import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.rpc.thrift.service.ThriftSocketIoPushService;

/**
 * 消息服务： 对外提供的rpc 接口发送socket.io事件
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/24 17:18
 */
@ThriftService(MicroServiceConstants.MESSAGE_NETTY_SERVICE)
public interface SocketIoMessagePushService extends ThriftSocketIoPushService {

}
