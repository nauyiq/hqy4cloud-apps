package com.hqy.cloud.apps.blog;

import com.corundumstudio.socketio.SocketIOServer;
import com.hqy.cloud.apps.blog.service.SocketIoBlogPushService;
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
import tk.mybatis.spring.annotation.MapperScan;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * start apps-blog-service.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/26 13:31
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
@Import(SpringContextHolder.class)
@MapperScan(basePackages = {"com.hqy.cloud.apps.blog.mapper"})
public class BlogServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(BlogServiceMain.class, args);
        ProjectContextInfo.startPrintf();
        SocketIOServer server = SpringContextHolder.getBean(SocketIOServer.class);
        initializeOnlineOfflineListener(server);
    }

    @Component
    @RequiredArgsConstructor
    public static class ThriftServerRegisterServer implements ThriftServerLauncher {
        private final SocketIoBlogPushService socketIoBlogPushService;
        @Override
        public List<RPCService> getRpcServices() {
            return Arrays.asList(socketIoBlogPushService);
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
