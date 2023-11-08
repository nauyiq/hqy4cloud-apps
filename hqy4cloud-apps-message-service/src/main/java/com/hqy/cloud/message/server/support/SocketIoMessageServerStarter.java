package com.hqy.cloud.message.server.support;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.socketio.starter.core.AbstractSocketIoServerStarter;
import com.hqy.cloud.socketio.starter.core.support.EventListener;
import com.hqy.cloud.util.config.ConfigurationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

import static com.hqy.cloud.common.base.config.ConfigConstants.SOCKET_IO_PORT;
import static com.hqy.cloud.message.common.Constants.DEFAULT_SOCKET_CONTEXT_PATH;
import static com.hqy.cloud.message.common.Constants.DEFAULT_SOCKET_PORT;
import static com.hqy.cloud.util.config.ConfigurationContext.YamlEnum.APPLICATION_YAML;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/23 17:30
 */
@Component
public class SocketIoMessageServerStarter extends AbstractSocketIoServerStarter {
    private final Environment environment;

    public SocketIoMessageServerStarter(Environment environment, NacosServiceManager nacosServiceManager) {
        super(environment, nacosServiceManager);
        this.environment = environment;
    }

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
        String property = environment.getProperty(SOCKET_IO_PORT);
        property = StringUtils.isBlank(property) ? ConfigurationContext.getString(APPLICATION_YAML, SOCKET_IO_PORT) : property;
        return StringUtils.isBlank(property) ? DEFAULT_SOCKET_PORT : Integer.parseInt(property);
    }

    @Override
    public String contextPath() {
        return DEFAULT_SOCKET_CONTEXT_PATH;
    }


}
