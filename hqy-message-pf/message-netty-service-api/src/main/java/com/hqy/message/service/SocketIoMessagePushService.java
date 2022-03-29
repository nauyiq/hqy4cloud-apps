package com.hqy.message.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.rpc.api.RPCService;

import java.util.Set;

/**
 * 消息服务： 对外提供的rpc 接口发送socket.io事件
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/24 17:18
 */
@ThriftService(MicroServiceConstants.MESSAGE_NETTY_SERVICE)
public interface SocketIoMessagePushService extends RPCService {

    /**
     * 单发消息-同步
     * @param bizId         发给谁
     * @param eventName     事件名
     * @param wsMessageJson 事件的json数据
     * @return 结果
     */
    @ThriftMethod
    boolean syncPush(@ThriftField(1) String bizId, @ThriftField(2) String eventName, @ThriftField(3) String wsMessageJson);

    /**
     * 群发消息-同步
     * @param bizIdSet      群发的用户集合
     * @param eventName     事件名
     * @param wsMessageJson 事件的json数据
     * @return 结果
     */
    @ThriftMethod
    boolean syncPushMultiple(@ThriftField(1) Set<String> bizIdSet, @ThriftField(2) String eventName, @ThriftField(3) String wsMessageJson);


    /**
     * 单发消息-异步
     * @param bizId         发给谁
     * @param eventName     事件名
     * @param wsMessageJson 事件的json数据
     * @return 结果
     */
    @ThriftMethod(oneway = true)
    boolean asyncPush(@ThriftField(1) String bizId, @ThriftField(2) String eventName, @ThriftField(3) String wsMessageJson);


    /**
     * 群发消息-异步
     * @param bizIdSet      群发的用户集合
     * @param eventName     事件名
     * @param wsMessageJson 事件的json数据
     * @return 结果
     */
    @ThriftMethod(oneway = true)
    boolean asyncPushMultiple(@ThriftField(1) Set<String> bizIdSet, @ThriftField(2) String eventName, @ThriftField(3) String wsMessageJson);

}
