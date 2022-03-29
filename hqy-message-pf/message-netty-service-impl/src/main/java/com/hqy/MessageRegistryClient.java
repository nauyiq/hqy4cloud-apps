package com.hqy;

import com.facebook.swift.service.ThriftServer;
import com.hqy.base.common.base.lang.ActuatorNodeEnum;
import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.ex.listener.DefaultAuthorizationListener;
import com.hqy.fundation.common.route.LoadBalanceHashFactorManager;
import com.hqy.fundation.common.route.SocketClusterStatus;
import com.hqy.fundation.common.route.SocketClusterStatusManager;
import com.hqy.message.server.EventMessageChatLauncher;
import com.hqy.rpc.nacos.AbstractNacosClientWrapper;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.rpc.thrift.ex.ThriftRpcHelper;
import com.hqy.socketio.SocketIOServer;
import com.hqy.util.AssertUtil;
import com.hqy.util.config.ConfigurationContext;
import com.hqy.util.spring.ProjectContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * rpc生产者客户端 将rpc、节点数据注册到nacos中
 * @author qiyuan.hong
 * @date 2022-03-23 22:12
 */
@Component
public class MessageRegistryClient extends AbstractNacosClientWrapper {

    private static final Logger log = LoggerFactory.getLogger(MessageRegistryClient.class);

    @Resource
    private ThriftServer tServer;
    @Resource
    private MessageThriftServer messageThriftServer;

    @Override
    public ClusterNode setProjectClusterNode() {

        //判断RPC服务是否启动
        boolean running = tServer.isRunning();
        if (!running) {
            //如果没有扫描到server 手动启动一下
            tServer.start();
        }
        log.info("@@@ Get ThriftServer success, running:{}", running);

        UsingIpPort uip = messageThriftServer.getUsingIpPort();
        AssertUtil.notNull(uip, "System error, Bind rpc port fail. please check thrift service");

        //定制化节点信息
        ClusterNode node = new ClusterNode();
        node.setNameEn(MicroServiceConstants.MESSAGE_NETTY_SERVICE);
        node.setName("聊天消息服务");
        node.setActuatorNode(ActuatorNodeEnum.CONSUMER);

        try {
            //通过配置文件获取socket.io项目的集群信息.
            boolean enableMultiNodes = Boolean.parseBoolean(ConfigurationContext.getProperty(ConfigurationContext.PropertiesEnum.SERVER_PROPERTIES,
                    BaseStringConstants.SocketProperties.ENABLE_MULTI_CLUSTER_NODES, "false"));
            int hash = Integer.parseInt(ConfigurationContext.getProperty(ConfigurationContext.PropertiesEnum.SERVER_PROPERTIES,
                    BaseStringConstants.SocketProperties.MULTI_CLUSTER_THIS_HASH, "0"));
            int countNodes = Integer.parseInt(ConfigurationContext.getProperty(ConfigurationContext.PropertiesEnum.SERVER_PROPERTIES,
                    BaseStringConstants.SocketProperties.COUNT_MULTI_CLUSTER_NODES, "1"));
            int port = Integer.parseInt(ConfigurationContext.getProperty(ConfigurationContext.PropertiesEnum.SERVER_PROPERTIES,
                    BaseStringConstants.SocketProperties.SOCKET_IO_PORT, "9007"));
            String contextPath = "/message/websocket";

            log.info("@@@ socket.io服务[{}]启动. port:{} hash:{}, contextPath:{}, countNodes:{}, enableMultiNodes:{}",
                    MicroServiceConstants.MESSAGE_NETTY_SERVICE, port, hash, contextPath, countNodes, enableMultiNodes);

            uip.setSocketPort(port);
            node.setUip(uip);

            //启动socketIo服务
            EventMessageChatLauncher launcher = new EventMessageChatLauncher();
            SocketIOServer socketIOServer = launcher.startUp(port, contextPath, new DefaultAuthorizationListener());

            //将SocketIOServer注册到ProjectContextInfo中
            ProjectContextInfo.setBean(socketIOServer);

            //将集群信息注册到redis
            SocketClusterStatusManager.registry(new SocketClusterStatus(MicroServiceConstants.MESSAGE_NETTY_SERVICE,
                    EnvironmentConfig.getInstance().getEnvironment(), countNodes, enableMultiNodes, contextPath));
            if (enableMultiNodes) {
                //注册当前的hash值到redis
                String hashFactor = ThriftRpcHelper.genHashFactor(uip.getIp(), port + "");
                node.setHash(hash);
                node.setHashFactor(hashFactor);
                LoadBalanceHashFactorManager.registry(MicroServiceConstants.MESSAGE_NETTY_SERVICE, hash, hashFactor);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(0);
        }

        return node;
    }
}
