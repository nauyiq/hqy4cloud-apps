package com.hqy.cloud.message.service.request.impl;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.corundumstudio.socketio.ex.SocketProjectContext;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.base.config.ConfigConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.foundation.common.route.SocketClusterStatus;
import com.hqy.cloud.foundation.common.route.SocketClusterStatusManager;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.bind.dto.MessagesRequestParamDTO;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.es.document.ImMessageDoc;
import com.hqy.cloud.message.es.service.ImMessageElasticService;
import com.hqy.cloud.message.service.ImFriendOperationsService;
import com.hqy.cloud.message.service.ImGroupOperationsService;
import com.hqy.cloud.message.service.ImMessageOperationsService;
import com.hqy.cloud.message.service.request.ImMessageRequestService;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImUserSettingTkService;
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
import java.util.Objects;
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

    private final ImUserSettingTkService imUserSettingTkService;
    private final ImConversationTkService conversationTkService;
    private final ImMessageElasticService imMessageElasticService;
    private final ImMessageOperationsService messageOperationsService;
    private final ImFriendOperationsService friendOperationsService;
    private final ImGroupOperationsService groupOperationsService;

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
    public R<PageResult<ImMessageVO>> getImMessages(Long id, MessagesRequestParamDTO params) {
        // query from es.
        ImConversation imConversation = conversationTkService.queryById(params.getConversationId());
        if (imConversation == null || (!id.equals(imConversation.getUserId()))) {
            return R.ok(new PageResult<>());
        }
        PageResult<ImMessageDoc> result = imMessageElasticService.queryPage(id, params);
        if (result == null || CollectionUtils.isEmpty(result.getResultList())) {
            return R.ok(new PageResult<>());
        }

        List<ImMessageDoc> resultList = result.getResultList();
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
        List<ImMessageVO> messages = resultList.stream().map(doc -> {
            Long from = doc.getFrom();
            String remark = friendRemarks.get(from.toString());
            AccountBaseInfoStruct struct = infoMap.get(from);
            if (struct == null) {
                return null;
            }
            return ImMessageVO.builder()
                    .id(doc.getMessageId())
                    .messageId(doc.getId().toString())
                    .isGroup(doc.getGroup())
                    .isRead(doc.getRead())
                    .fromUser(new UserInfoVO(from.toString(), struct.username, struct.nickname, struct.avatar, remark))
                    .toContactId(doc.getTo().toString())
                    .content(doc.getType().equals(ImMessageType.TEXT.type) ? doc.getContent() : doc.getPath())
                    .status(doc.getStatus() ? IM_MESSAGE_SUCCESS : IM_MESSAGE_FAILED)
                    .type(doc.getType())
                    .sendTime(doc.getCreated())
                    .build();
        }).filter(Objects::nonNull).sorted((m1, m2) -> (int) (m1.getSendTime() - m2.getSendTime())).toList();
        return R.ok(new PageResult<>(result.getCurrentPage(), params.getLimit(), result.getTotal(), messages));
    }

    @Override
    public R<ImMessageVO> sendImMessage(Long id, ImMessageDTO message) {
        Long to = Long.parseLong(message.getToContactId());
        //check user enable chat.
        if (message.getIsGroup()) {
            if (groupOperationsService.isGroupMember(id, to)) {
                return R.failed(AppsResultCode.IM_NOT_GROUP_MEMBER);
            }
        } else {
            if (!friendOperationsService.isFriend(id, to) && !imUserSettingTkService.enabledPrivateChat(id)) {
                return R.failed(AppsResultCode.IM_NOT_FRIEND);
            }
        }
        ImMessageVO messageVo = messageOperationsService.sendImMessage(id, message);
        return R.ok(messageVo);
    }

    @Override
    public R<List<String>> setMessageRead(Long id, MessageUnreadDTO dto) {
        //query conversation from db.
        ImConversation conversation;
        if (dto.getConversationId() != null) {
            conversation = conversationTkService.queryById(dto.getConversationId());
        } else {
            conversation = conversationTkService.queryOne(ImConversation.of(dto.getFrom(), dto.getTo(), false));
        }
        //check conversation
        if (conversation == null || !id.equals(conversation.getUserId())) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        List<String> messageIds = messageOperationsService.readMessages(conversation);
        return R.ok(messageIds);
    }
}
