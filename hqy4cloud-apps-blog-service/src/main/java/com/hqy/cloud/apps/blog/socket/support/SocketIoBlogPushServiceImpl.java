package com.hqy.cloud.apps.blog.socket.support;

import com.hqy.cloud.apps.blog.service.SocketIoBlogPushService;
import com.hqy.cloud.socketio.starter.service.AbstractThriftSocketIoPushService;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/1 17:40
 */
@Service
public class SocketIoBlogPushServiceImpl extends AbstractThriftSocketIoPushService implements SocketIoBlogPushService {
}
