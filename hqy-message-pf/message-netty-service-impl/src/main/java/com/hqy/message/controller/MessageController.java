package com.hqy.message.controller;

import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.ex.SocketProjectContext;
import com.hqy.foundation.common.bind.SocketIoConnection;
import com.hqy.fundation.common.route.SocketClusterStatus;
import com.hqy.fundation.common.route.SocketClusterStatusManager;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.socketio.Configuration;
import com.hqy.socketio.SocketIOServer;
import com.hqy.util.IpUtil;
import com.hqy.util.JwtUtil;
import com.hqy.util.RequestUtil;
import com.hqy.util.config.ConfigurationContext;
import com.hqy.util.spring.ProjectContextInfo;
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
    public DataResponse genWsMessageConnection(HttpServletRequest request) {
        try {
            String bizId = RequestUtil.getBizIdFromHttpRequestHeader(request);
            if (StringUtils.isBlank(bizId)) {
                return new DataResponse(false, CommonResultCode.INVALID_ACCESS_TOKEN.message, CommonResultCode.INVALID_ACCESS_TOKEN.code);
            }
            SocketIOServer socketIOServer = ProjectContextInfo.getBean(SocketIOServer.class);
            Configuration configuration = socketIOServer.getConfiguration();
            //wtoken jwt payload obj
            SocketProjectContext wTokenPayload = new SocketProjectContext(new SocketProjectContext.App(MicroServiceConstants.MESSAGE_NETTY_SERVICE), bizId);
            //access host
            String host = ConfigurationContext.getProperty(ConfigurationContext.PropertiesEnum.SERVER_PROPERTIES, HOST);
            if (StringUtils.isBlank(host) && EnvironmentConfig.getInstance().isDevEnvironment()) {
                String hostAddress = IpUtil.getHostAddress();
                String port = CommonSwitcher.ENABLE_CUSTOMER_GATEWAY_LOAD_BALANCE.isOn() ? "9527" : configuration.getPort() + "";
                host = "http://" + hostAddress  + BaseStringConstants.Symbol.COLON + port;
            }
            //构建SocketIoConnection
            String context = configuration.getContext();
            SocketIoConnection socketIoConnection = new SocketIoConnection(host, context);
            String wtoken = JwtUtil.sign(wTokenPayload);

            //判断是否集群启动
            SocketClusterStatus query = SocketClusterStatusManager.query(EnvironmentConfig.getInstance().getEnvironment(), MicroServiceConstants.MESSAGE_NETTY_SERVICE);
            if (query.isEnableMultiWsNode()) {
                int hash = query.getSocketIoPathHashMod(bizId);
                wtoken = wtoken + "&hash=" + hash;
            }
            socketIoConnection.setWtoken(wtoken);
            socketIoConnection.setConnectUrl(host + context + "?wtoken=" + wtoken);

            return new DataResponse(true, CommonResultCode.SUCCESS.message, CommonResultCode.SUCCESS.code, socketIoConnection);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new DataResponse(false, CommonResultCode.SYSTEM_BUSY.message, CommonResultCode.SYSTEM_BUSY.code, null);
        }
    }

}
