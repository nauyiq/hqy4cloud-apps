package com.hqy;

import com.hqy.fundation.common.rpc.api.RPCService;
import com.hqy.rpc.api.AbstractThriftServer;
import org.springframework.stereotype.Component;

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
        return null;
    }
}
