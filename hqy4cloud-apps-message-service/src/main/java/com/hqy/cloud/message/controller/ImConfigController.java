package com.hqy.cloud.message.controller;

import com.hqy.cloud.netty.socketio.SocketIoEndpoint;
import com.hqy.cloud.netty.socketio.SocketIoSocketServer;
import com.hqy.cloud.socket.cluster.client.support.SocketClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/29 11:20
 */
@Slf4j
@RestController
@RequestMapping("/im")
public class ImConfigController extends SocketIoEndpoint {

    public ImConfigController(SocketIoSocketServer ioSocketServer, SocketClient client) {
        super(ioSocketServer, client);
    }

}
