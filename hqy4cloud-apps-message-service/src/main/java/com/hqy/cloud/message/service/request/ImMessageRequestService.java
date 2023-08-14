package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.foundation.common.bind.SocketIoConnection;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26 13:19
 */
public interface ImMessageRequestService {


    /**
     * 获取socket.io连接
     * @param request req
     * @param bizId   socketio connection id
     * @return {@link SocketIoConnection}
     */
    R<SocketIoConnection> genWsMessageConnection(HttpServletRequest request, String bizId);

}
