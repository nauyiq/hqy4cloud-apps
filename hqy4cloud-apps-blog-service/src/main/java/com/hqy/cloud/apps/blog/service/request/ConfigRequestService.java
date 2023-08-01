package com.hqy.cloud.apps.blog.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.foundation.common.bind.SocketIoConnection;

import javax.servlet.http.HttpServletRequest;

/**
 * ConfigRequestService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/11/4 13:31
 */
public interface ConfigRequestService {

    /**
     * 获取关于我.
     * @return R.
     */
    R<String> getAboutMe();

    /**
     * 获取socket.io连接
     * @param request 请求.
     * @param bizId   业务ID， 用户名
     * @return        {@link SocketIoConnection}
     */
    R<SocketIoConnection> genWsBlogConnection(HttpServletRequest request, String bizId);
}
