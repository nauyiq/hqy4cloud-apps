package com.hqy.server;

import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.socketio.AuthorizationListener;
import com.hqy.socketio.Configuration;
import com.hqy.socketio.SocketIOServer;
import com.hqy.util.config.ConfigurationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * 聊天消息事件启动器. 用于启动socket.io服务
 * @author qiyuan.hong
 * @date 2022-03-23 22:16
 */
public class EventMessageChatLauncher {

    private static final Logger log = LoggerFactory.getLogger(EventMessageChatLauncher.class);

    public EventMessageChatLauncher() {
    }

    /**
     * 启动socket.io服务
     * @param port socket.io端口
     * @param contextPath 握手的contextPath
     * @param authorizationListener 握手验证监听器
     * @return SocketIOServer
     */
    public SocketIOServer startUp(int port, String contextPath, AuthorizationListener authorizationListener) {
        log.info("@@@ EventMessageChatLauncher start netty server, part:{}, contextPath:{}", port, contextPath);
        //SocketIo配置类
        Configuration config = new Configuration();
        config.setAuthorizationListener(authorizationListener);
        config.setContext(contextPath);
        config.setPort(port);

        if (EnvironmentConfig.getInstance().isDevEnvironment() || CommonSwitcher.CONFIG_SSL_BY_NGINX_PROXY.isOn()) {
            log.info("@@@ 开发环境或者Nginx方向代理情况下 不启用ssl加密传输.");
        } else {
            String keystore = ConfigurationContext.getProperties(ConfigurationContext.PropertiesEnum.SERVER_PROPERTIES).
                    getProperty(BaseStringConstants.Auth.SOCKET_SSL_KEYSTORE_KEY, "/hqy.keystore");
            String password = ConfigurationContext.getProperties(ConfigurationContext.PropertiesEnum.SERVER_PROPERTIES).
                    getProperty(BaseStringConstants.Auth.SOCKET_SSL_KEYSTORE_PASSWORD, "hongqy@2022");
            log.info("@@@ 非开发环境需要加载keystore, keystore:{}", keystore);
            config.setKeyStorePassword(password);
            try (InputStream inputStream = SocketIOServer.class.getResourceAsStream(keystore)){
                config.setKeyStore(inputStream);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        log.info("@@@ Socket.io config:{}", config);

        final SocketIOServer socketIOServer = new SocketIOServer(config);
        socketIOServer.start();
        //TODO addEvent

        log.info("@@@ SocketIoServer start() OK.");
        return socketIOServer;

    }
}
