package com.hqy.cloud.message.service.request.impl;

import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.ConvertUtil;
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
import com.hqy.cloud.message.tk.entity.ImMessage;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImMessageTkService;
import com.hqy.cloud.message.tk.service.ImUserSettingTkService;
import com.hqy.cloud.util.thread.ParentExecutorService;
import com.hqy.cloud.web.common.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
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

    private final ImUserSettingTkService imUserSettingTkService;
    private final ImConversationTkService conversationTkService;
    private final ImMessageElasticService imMessageElasticService;
    private final ImMessageOperationsService messageOperationsService;
    private final ImFriendOperationsService friendOperationsService;
    private final ImGroupOperationsService groupOperationsService;
    private final ImMessageTkService imMessageTkService;

    @Override
    public R<PageResult<ImMessageVO>> getImMessages(Long id, MessagesRequestParamDTO params) {
        // query im conversation from db.
        ImConversation imConversation = conversationTkService.queryById(params.getConversationId());
        if (imConversation == null || (!id.equals(imConversation.getUserId()))) {
            return R.ok(new PageResult<>());
        }
        params.setToContactId(imConversation.getContactId());
        params.setIsGroup(imConversation.getGroup());
        // query message history from es.
        PageResult<ImMessageDoc> result = imMessageElasticService.queryPage(id, imConversation.getRemove(), params);
        if (result == null || CollectionUtils.isEmpty(result.getResultList())) {
            return R.ok(new PageResult<>());
        }
        long total = result.getTotal();
        // remove self system message
        List<ImMessageDoc> resultList = result.getResultList();
        if (resultList.parallelStream().anyMatch(doc -> ImMessageType.SYSTEM.type.equals(doc.getType()))) {
            Iterator<ImMessageDoc> iterator = resultList.iterator();
            while (iterator.hasNext()) {
                ImMessageDoc doc = iterator.next();
                if (ImMessageType.SYSTEM.type.equals(doc.getType()) && id.equals(doc.getFrom())) {
                    iterator.remove();
                    total--;
                }
            }
        }
        // query account info by account rpc.
        List<Long> ids;
        if (imConversation.getGroup()) {
            ids = resultList.parallelStream().map(ImMessageDoc::getFrom).distinct().collect(Collectors.toList());
        } else {
            ids = Arrays.asList(imConversation.getUserId(), imConversation.getContactId());
        }
        Map<Long, AccountProfileStruct> profileMap = AccountRpcUtil.getAccountProfileMap(ids);
        //convert to message vo
        List<ImMessageVO> messages = convertMessages(resultList, profileMap, ids, imConversation);
        // setting messages read.
        if (resultList.parallelStream().anyMatch(doc -> doc.getRead() != null && !doc.getRead())) {
            ParentExecutorService.getInstance().execute(() -> messageOperationsService.readMessages(imConversation));
        }
        return R.ok(new PageResult<>(result.getCurrentPage(), params.getLimit(), total, messages));
    }

    private List<ImMessageVO> convertMessages(List<ImMessageDoc> resultList, Map<Long, AccountProfileStruct> profileMap, List<Long> ids, ImConversation imConversation) {
        Map<Long, String> remarks;
        if (imConversation.getGroup()) {
            remarks = groupOperationsService.getGroupMemberRemark(imConversation.getContactId(), ids);
        } else {
            remarks = friendOperationsService.getFriendRemarks(imConversation.getUserId(), Collections.singletonList(imConversation.getContactId()));
        }
        return resultList.stream().map(doc -> {
            Long from = doc.getFrom();
            //from user account info.
            AccountProfileStruct struct = profileMap.get(from);
            if (struct == null) {
                return null;
            }
            String remark = remarks.get(from);
            remark = (StringUtils.isBlank(remark) || remark.equals(StringConstants.TRUE) || remark.equals(StringConstants.FALSE)) ? struct.nickname : remark;
            return ImMessageVO.builder().id(doc.getMessageId())
                    .messageId(doc.getId().toString())
                    .isGroup(doc.getGroup())
                    .isRead(true)
                    .fromUser(new UserInfoVO(from.toString(), struct.username, struct.nickname, struct.avatar, remark))
                    .toContactId(doc.getTo().toString())
                    .content(ConvertUtil.getMessageContent(imConversation.getUserId(), doc))
                    .status(doc.getStatus() ? IM_MESSAGE_SUCCESS : IM_MESSAGE_FAILED)
                    .type(doc.getType())
                    .sendTime(doc.getCreated())
                    .build();
        }).filter(Objects::nonNull).sorted((m1, m2) -> (int) (m1.getSendTime() - m2.getSendTime())).toList();
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
        return messageVo == null ? R.failed() : R.ok(messageVo);
    }

    @Override
    public R<List<String>> setMessageRead(Long id, MessageUnreadDTO dto) {
        //query conversation from db.
        ImConversation conversation;
        if (dto.getConversationId() != null) {
            conversation = conversationTkService.queryById(dto.getConversationId());
        } else {
            conversation = conversationTkService.queryOne(ImConversation.of(id, dto.getFrom(), false));
        }
        //check conversation
        if (conversation == null || !id.equals(conversation.getUserId())) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        List<String> messageIds = messageOperationsService.readMessages(conversation);
        return R.ok(messageIds);
    }

    @Override
    public R<Boolean> undoMessage(Long id, Long messageId) {
        ImMessage imMessage = imMessageTkService.queryById(messageId);
        if (imMessage == null || !id.equals(imMessage.getFrom())) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        return messageOperationsService.undoMessage(imMessage) ? R.ok() : R.failed();
    }
}
