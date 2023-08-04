package com.hqy.cloud.apps.blog.service.request.impl;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.corundumstudio.socketio.ex.SocketProjectContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.cloud.apps.blog.entity.Config;
import com.hqy.cloud.apps.blog.service.opeations.BlogDbOperationService;
import com.hqy.cloud.apps.blog.service.request.ConfigRequestService;
import com.hqy.cloud.common.base.config.ConfigConstants;
import com.hqy.cloud.common.base.lang.NumberConstants;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.socketio.starter.core.SocketIoServerStarter;
import com.hqy.cloud.socketio.starter.core.support.SocketIoConnectionUtil;
import com.hqy.cloud.util.crypto.symmetric.JWT;
import com.hqy.foundation.common.bind.SocketIoConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/11/4 13:32
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigRequestServiceImpl implements ConfigRequestService {
    private final SocketIoServerStarter starter;
    private final BlogDbOperationService blogDbOperationService;
    private final NacosServiceManager nacosServiceManager;
    private final Environment environment;

    private static final Cache<String, Config> CACHE = CacheBuilder.newBuilder().maximumSize(1).initialCapacity(1)
             .expireAfterWrite(NumberConstants.ONE_HOUR_4MILLISECONDS, TimeUnit.MILLISECONDS).build();

    private final String key =  ConfigRequestService.class.getSimpleName();

    @Override
    public R<String> getAboutMe() {
        Config config = CACHE.getIfPresent(key);
        if (config == null) {
            List<Config> configs = blogDbOperationService.configTkService().queryAll();
            if (CollectionUtils.isEmpty(configs)) {
                return R.ok(StringConstants.EMPTY);
            } else {
                CACHE.put(key, configs.get(0));
                return R.ok(configs.get(0).getAboutMe());
            }
        } else {
          return R.ok(config.getAboutMe());
        }
    }

    @Override
    public R<SocketIoConnection> genWsBlogConnection(HttpServletRequest request, String bizId) {
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

        String connectionUrl = host + starter.contextPath();
        SocketIoConnection connection = new SocketIoConnection(connectionUrl, authorization, starter.contextPath(), host);
        return R.ok(connection);
    }
}
