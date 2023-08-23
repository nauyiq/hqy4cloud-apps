package com.hqy.cloud.message.service.request.impl;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.corundumstudio.socketio.ex.SocketProjectContext;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.cloud.common.base.config.ConfigConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.message.bind.dto.MessagesRequestParamDTO;
import com.hqy.cloud.message.bind.vo.MessageVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.es.document.ImMessageDoc;
import com.hqy.cloud.message.es.service.ImMessageElasticService;
import com.hqy.cloud.message.service.ImFriendOperationsService;
import com.hqy.cloud.message.service.request.ImMessageRequestService;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.socketio.starter.core.SocketIoServerStarter;
import com.hqy.cloud.socketio.starter.core.support.SocketIoConnectionUtil;
import com.hqy.cloud.util.crypto.symmetric.JWT;
import com.hqy.cloud.web.common.AccountRpcUtil;
import com.hqy.foundation.common.bind.SocketIoConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_MESSAGE_FAILED;
import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_MESSAGE_SUCCESS;

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
    private final ImConversationTkService conversationTkService;
    private final ImMessageElasticService imMessageElasticService;
    private final ImFriendOperationsService friendOperationsService;

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

    @Override
    public R<PageResult<MessageVO>> getImMessages(Long id, MessagesRequestParamDTO params) {
        // query from es.
        ImConversation imConversation = conversationTkService.queryById(params.getConversationId());
        if (imConversation == null || (!id.equals(imConversation.getUserId()))) {
            return R.ok(new PageResult<>());
        }
        PageResult<ImMessageDoc> result = imMessageElasticService.queryPage(params);
        if (result == null || CollectionUtils.isEmpty(result.getResultList())) {
            return R.ok(new PageResult<>());
        }
        List<ImMessageDoc> resultList = result.getResultList();
        //abstain users info
        List<Long> ids;
        if (imConversation.getGroup()) {
            ids = resultList.parallelStream().map(ImMessageDoc::getFrom).distinct().collect(Collectors.toList());
        } else {
            ids = Arrays.asList(imConversation.getUserId(), imConversation.getContactId());
        }
        //TODO 如果是群里应该还要获取群聊用户的在群里的备注昵称.
        Map<String, String> friendRemarks = friendOperationsService.getFriendRemarks(id);
        Map<Long, AccountBaseInfoStruct> infoMap = AccountRpcUtil.getAccountBaseInfoMap(ids);
        //convert to message vo
        List<MessageVO> messages = resultList.stream().map(doc -> {
            Long from = doc.getFrom();
            String remark = friendRemarks.get(from.toString());
            AccountBaseInfoStruct struct = infoMap.get(from);
            if (struct == null) {
                return null;
            }
            return MessageVO.builder()
                    .id(doc.getId().toString())
                    .messageId(doc.getMessageId())
                    .isGroup(doc.getGroup())
                    .isRead(doc.getRead())
                    .fromUser(new UserInfoVO(from.toString(), struct.username, struct.nickname, struct.avatar, remark))
                    .toContactId(doc.getTo().toString())
                    .status(doc.getStatus() ? IM_MESSAGE_SUCCESS : IM_MESSAGE_FAILED)
                    .type(doc.getType())
                    .sendTime(doc.getCreated())
                    .build();
        }).toList();
        return R.ok(new PageResult<>(result.getCurrentPage(), result.getPages(), result.getTotal(), messages));
    }
}
