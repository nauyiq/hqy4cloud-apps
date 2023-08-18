package com.hqy.cloud.message.service.request.impl;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.corundumstudio.socketio.ex.SocketProjectContext;
import com.hqy.cloud.common.base.config.ConfigConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.message.service.request.ImMessageRequestService;
import com.hqy.cloud.socketio.starter.core.SocketIoServerStarter;
import com.hqy.cloud.socketio.starter.core.support.SocketIoConnectionUtil;
import com.hqy.cloud.util.crypto.symmetric.JWT;
import com.hqy.foundation.common.bind.SocketIoConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26 13:21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImMessageRequestServiceImpl implements ImMessageRequestService {
    private final SocketIoServerStarter starter;
    private final Environment environment;
    private final NacosServiceManager nacosServiceManager;

    @Override
    public R<SocketIoConnection> genWsMessageConnection(HttpServletRequest request, String bizId) {
        //abstain access host
        String host = environment.getProperty(ConfigConstants.SOCKET_CONNECTION_HOST);
        host = StringUtils.isBlank(host) ? SocketIoConnectionUtil.getSocketHost(starter.serverPort()) : host;
        //abstain handshake authorization
        SocketProjectContext context = SocketProjectContext.of(starter.serviceName(), bizId);
        String authorization = JWT.getInstance(starter.authorizationSecret()).encrypt(context);
        //check enable cluster
        SocketClusterStatus query = SocketClusterStatusManager.query(com.hqy.cloud.rpc.core.Environment.getInstance().getEnvironment(), starter.serviceName());
        if (query.isEnableMultiWsNode()) {
            NamingService namingService = nacosServiceManager.getNamingService();
            int hash;
            try {
                List<Instance> instances = namingService.selectInstances(starter.serviceName(), true);
                hash = query.getSocketIoPathHashMod(bizId, instances.size());
            } catch (NacosException e) {
                log.warn("Failed execute to select instances by nacos.");
                hash = query.getSocketIoPathHashMod(bizId);
            }
            authorization = authorization + "&hash=" + hash;
        }
        SocketIoConnection connection = new SocketIoConnection(host, authorization, starter.contextPath(), host);
        return R.ok(connection);
    }
}
