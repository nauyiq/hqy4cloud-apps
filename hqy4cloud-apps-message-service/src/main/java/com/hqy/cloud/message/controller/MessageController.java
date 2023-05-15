package com.hqy.cloud.message.controller;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.ex.SocketProjectContext;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.util.config.ConfigurationContext;
import com.hqy.cloud.util.crypto.symmetric.JWT;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.foundation.common.bind.SocketIoConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/1 13:38
 */
@Slf4j
@RestController
public class MessageController {

    private static final String HOST = "socket.message.host";

    /**
     * 获取消息聊天服务的socket.io连接
     * @param request  HttpServletRequest
     * @return  response
     */
    @GetMapping("/message/connection")
    public R<SocketIoConnection> genWsMessageConnection(HttpServletRequest request) {
        try {
            AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
            String bizId = authentication.getName();
            SocketIOServer server = ProjectContextInfo.getBean(SocketIOServer.class);
            Configuration configuration = server.getConfiguration();
            //wtoken jwt payload obj
            SocketProjectContext wTokenPayload = new SocketProjectContext(new SocketProjectContext.App(MicroServiceConstants.MESSAGE_NETTY_SERVICE), bizId);
            //access host
            String host = ConfigurationContext.getProperty(ConfigurationContext.PropertiesEnum.SERVER_PROPERTIES, HOST);
            if (StringUtils.isBlank(host) && Environment.getInstance().isDevEnvironment()) {
                String hostAddress = IpUtil.getHostAddress();
                String port = CommonSwitcher.ENABLE_CUSTOMER_GATEWAY_LOAD_BALANCE.isOn() ? "9527" : configuration.getPort() + "";
                host = "http://" + hostAddress  + StringConstants.Symbol.COLON + port;
            }
            //构建SocketIoConnection
            String context = configuration.getContext();
            SocketIoConnection socketIoConnection = new SocketIoConnection(host, context);
            String wtoken = JWT.getInstance().encrypt(wTokenPayload);

            //判断是否集群启动
            SocketClusterStatus query = SocketClusterStatusManager.query(Environment.getInstance().getEnvironment(), MicroServiceConstants.MESSAGE_NETTY_SERVICE);
            if (query.isEnableMultiWsNode()) {
                int hash = query.getSocketIoPathHashMod(bizId);
                wtoken = wtoken + "&hash=" + hash;
            }
            socketIoConnection.setWtoken(wtoken);
            socketIoConnection.setConnectUrl(host + context + "?wtoken=" + wtoken);

            return R.ok(socketIoConnection);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.failed();
        }
    }

}
