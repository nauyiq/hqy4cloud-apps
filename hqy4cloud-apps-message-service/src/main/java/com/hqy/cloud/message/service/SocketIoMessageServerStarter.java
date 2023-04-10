package com.hqy.cloud.message.service;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.ex.listener.DefaultAuthorizationListener;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.socketio.starter.core.SocketIoServerStarter;
import com.hqy.cloud.socketio.starter.core.support.EventListener;
import com.hqy.cloud.util.config.ConfigurationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/23 17:30
 */
@Component
public class SocketIoMessageServerStarter implements SocketIoServerStarter {

    @Override
    public String serviceName() {
        return MicroServiceConstants.MESSAGE_NETTY_SERVICE;
    }

    @Override
    public Set<EventListener> eventListeners() {
        return Collections.emptySet();
    }

    @Override
    public int serverPort() {
        return Integer.parseInt(ConfigurationContext.getProperty(ConfigurationContext.PropertiesEnum.SERVER_PROPERTIES,
                StringConstants.SocketProperties.SOCKET_IO_PORT, "9007"));
    }

    @Override
    public String contextPath() {
        return "/message/websocket";
    }

    @Override
    public AuthorizationListener authorizationListener() {
        return new DefaultAuthorizationListener();
    }
}
