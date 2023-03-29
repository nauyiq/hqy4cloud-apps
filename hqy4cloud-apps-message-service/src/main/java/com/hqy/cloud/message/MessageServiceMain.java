package com.hqy.cloud.message;

import com.corundumstudio.socketio.SocketIOServer;
import com.hqy.cloud.message.service.SocketIoMessagePushService;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.thrift.service.ThriftServerLauncher;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 * 基于netty的socket.io服务 <br>
 * @author qiyuan.hong
 * @date 2022-03-23 22:08
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@Import(SpringContextHolder.class)
public class MessageServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(MessageServiceMain.class, args);
        ProjectContextInfo.startPrintf();
        SocketIOServer server = SpringContextHolder.getBean(SocketIOServer.class);
        initializeOnlineOfflineListener(server);
    }

    @Component
    @RequiredArgsConstructor
    public static class ThriftServerRegisterServer implements ThriftServerLauncher {
        private final SocketIoMessagePushService socketIoMessagePushService;
        @Override
        public List<RPCService> getRpcServices() {
            return Arrays.asList(socketIoMessagePushService);
        }
    }


    private static void initializeOnlineOfflineListener(SocketIOServer server) {
        server.addConnectListener(client -> {
            UUID sessionId = client.getSessionId();
            log.info("@@@ 新连接上线: sessionId = {}", sessionId);
        });

        server.addDisconnectListener(client -> {
            UUID sessionId = client.getSessionId();
            log.info("@@@ 旧连接断开: sessionId = {}", sessionId);
        });
    }


}
