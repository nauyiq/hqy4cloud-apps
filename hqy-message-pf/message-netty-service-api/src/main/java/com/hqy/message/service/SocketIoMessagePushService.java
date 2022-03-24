package com.hqy.message.service;

import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.rpc.api.RPCService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/24 17:18
 */
@ThriftService(MicroServiceConstants.MESSAGE_NETTY_SERVICE)
public interface SocketIoMessagePushService extends RPCService {
}
