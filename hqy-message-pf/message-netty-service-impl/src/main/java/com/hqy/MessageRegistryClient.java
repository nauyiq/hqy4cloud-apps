package com.hqy;

import com.hqy.rpc.nacos.AbstractNacosClientWrapper;
import com.hqy.rpc.regist.ClusterNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * rpc生产者客户端 将rpc、节点数据注册到nacos中
 * @author qiyuan.hong
 * @date 2022-03-23 22:12
 */
@Component
public class MessageRegistryClient extends AbstractNacosClientWrapper {

    private static final Logger log = LoggerFactory.getLogger(MessageRegistryClient.class);

    @Override
    public ClusterNode setProjectClusterNode() {
        return null;
    }
}
