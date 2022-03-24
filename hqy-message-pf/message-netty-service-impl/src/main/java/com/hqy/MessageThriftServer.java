package com.hqy;

import com.hqy.base.common.rpc.api.RPCService;
import com.hqy.message.service.SocketIoMessagePushService;
import com.hqy.rpc.api.AbstractThriftServer;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * 注册需要暴露的rpc接口.（由父类进行统一的注册）
 * @author qiyuan.hong
 * @date 2022-03-23 22:12
 */
@Component
public class MessageThriftServer extends AbstractThriftServer {

    @Override
    public List<RPCService> getServiceList4Register() {
        List<RPCService> rpcServices = new ArrayList<>();
        rpcServices.add(SpringContextHolder.getBean(SocketIoMessagePushService.class));
        return rpcServices;
    }
}
