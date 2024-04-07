package com.hqy.cloud.message;

import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.registry.config.deploy.EnableDeployClient;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

/**
 * 基于netty的socket.io服务 <br>
 * @author qiyuan.hong
 * @date 2022-03-23 22:08
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableDeployClient(actuatorType = ActuatorNode.SOCKETIO_SERVER)
@MapperScan(basePackages = "com.hqy.cloud.message.db.mapper")
@Import(SpringContextHolder.class)
public class MessageServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(MessageServiceMain.class, args);
    }

}
