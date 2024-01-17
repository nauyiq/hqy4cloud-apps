package com.hqy.cloud.message.service.request.impl;

import cn.hutool.core.io.file.FileNameUtil;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.ConvertUtil;
import com.hqy.cloud.message.bind.dto.ForwardMessageDTO;
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
import com.hqy.cloud.util.ProjectExecutors;
import com.hqy.cloud.util.file.FileValidateContext;
import com.hqy.cloud.web.common.AccountRpcUtil;
import com.hqy.cloud.web.common.UploadResult;
import com.hqy.cloud.web.upload.UploadFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.*;

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
    private final UploadFileService uploadFileService;

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
        PageResult<ImMessageDoc> result = imMessageElasticService.queryPage(id, imConversation.getLastRemoveTime(), imConversation.getDeleted(), params);
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
        if (resultList.parallelStream().filter(doc -> doc.getTo().equals(id)).anyMatch(doc -> doc.getRead() != null && !doc.getRead())) {
            ProjectExecutors.getInstance().execute(() -> messageOperationsService.readMessages(imConversation));
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
                    .isRead(doc.getRead())
                    .fromUser(new UserInfoVO(from.toString(), struct.username, struct.nickname, struct.avatar, remark))
                    .toContactId(doc.getTo().toString())
                    .content(ConvertUtil.getMessageContent(imConversation.getUserId(), struct.username ,doc))
                    .fileSize(doc.getFileSize())
                    .fileName(doc.getFileName())
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
            if (!groupOperationsService.isGroupMember(id, to)) {
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
    public R<ImMessageVO> sendImFileMessage(Long id, MultipartFile file, ImMessageDTO message) {
        Long to = Long.parseLong(message.getToContactId());
        //check user enable chat.
        if (message.getIsGroup()) {
            if (!groupOperationsService.isGroupMember(id, to)) {
                return R.failed(AppsResultCode.IM_NOT_GROUP_MEMBER);
            }
        } else {
            if (!friendOperationsService.isFriend(id, to) && !imUserSettingTkService.enabledPrivateChat(id)) {
                return R.failed(AppsResultCode.IM_NOT_FRIEND);
            }
        }
        // 上传文件
        UploadResult result = uploadFileService.uploadFile(message.getIsGroup() ? UPLOAD_IM_GROUP_FOLDER : UPLOAD_IM_PRIVATE_FOLDER, file).getResult();
        if (!result.isResult()) {
            return R.failed(result.getMessage());
        }
        String path = result.getPath();
        ImMessageType messageType = FileValidateContext.isSupportedImgFileType(FileNameUtil.extName(path)) ? ImMessageType.IMAGE : ImMessageType.FILE;
        message.setType(messageType.type);
        message.setContent(result.getPath());
        message.setFileSize(file.getSize());
        message.setFileName(file.getOriginalFilename());
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
            conversation = conversationTkService.queryOne(ImConversation.of(id, dto.getUserId(), dto.getIsGroup()));
        }
        //check conversation
        if (conversation == null || !id.equals(conversation.getUserId())) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        List<String> messageIds = messageOperationsService.readMessages(conversation);
        return R.ok(messageIds);
    }

    @Override
    public R<Boolean> undoMessage(AuthenticationInfo authentication, Long messageId) {
        ImMessage imMessage = imMessageTkService.queryById(messageId);
        if (imMessage == null || !authentication.getId().equals(imMessage.getFrom())) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        return messageOperationsService.undoMessage(authentication.getName(), imMessage) ? R.ok() : R.failed();
    }

    @Override
        public R<List<ImMessageVO>> forwardMessage(Long id, ForwardMessageDTO forwardMessage) {
        Long messageId = forwardMessage.getMessageId();
        ImMessage message = imMessageTkService.queryById(messageId);
        if (message == null || message.getType().equals(ImMessageType.EVENT.type)) {
            return R.failed(ResultCode.DATA_EMPTY);
        }
        List<ForwardMessageDTO.Forward> forwards = forwardMessage.getForwards();
        Map<Boolean, List<ForwardMessageDTO.Forward>> map = forwards.parallelStream().collect(Collectors.groupingBy(ForwardMessageDTO.Forward::getGroup));
        //转发私聊列表
        List<ForwardMessageDTO.Forward> privateForwards = map.get(Boolean.FALSE);
        if (CollectionUtils.isNotEmpty(privateForwards)) {
            // 判断转发私聊列表是否是好友或者用户允许私聊
            List<Long> userIds = privateForwards.parallelStream().map(ForwardMessageDTO.Forward::getContactId).toList();
            Map<Long, String> friendRemarks = friendOperationsService.getFriendRemarks(id, userIds);
            if (!checkEnabledPrivateChat(userIds, friendRemarks)) {
                return R.failed(AppsResultCode.IM_NOT_FRIEND);
            }
        }
        return  R.ok(messageOperationsService.forwardMessage(id, message, forwards));
    }

    private boolean checkEnabledPrivateChat(List<Long> userIds, Map<Long, String> friendRemarks) {
        List<Long> notFriends = userIds.stream().filter(userId -> !friendRemarks.containsKey(userId)).toList();
        if (notFriends.size() == 0) {
            return true;
        }
        return imUserSettingTkService.allEnablePrivateChat(notFriends);
    }
}
