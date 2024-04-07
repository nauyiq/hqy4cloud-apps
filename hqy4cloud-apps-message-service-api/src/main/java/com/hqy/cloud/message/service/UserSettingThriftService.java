package com.hqy.cloud.message.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.thrift.struct.CommonResultStruct;
import com.hqy.cloud.rpc.transaction.GlobalRemoteTransactional;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/20
 */
@ThriftService(MicroServiceConstants.MESSAGE_NETTY_SERVICE)
public interface UserSettingThriftService extends RPCService {

    /**
     * 添加聊天用户
     * @param id       用户id
     * @param username 用户名
     * @param nickname 用户昵称
     * @param avatar   头像
     * @return         结果
     */
    @ThriftMethod
    @GlobalRemoteTransactional
    boolean addImUser(@ThriftField(1)Long id, @ThriftField(2)String username,
                                 @ThriftField(3)String nickname, @ThriftField(4) String avatar);

}
