package com.hqy.cloud.message.service.impl;

import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.Constants;
import com.hqy.cloud.message.bind.dto.*;
import com.hqy.cloud.message.bind.enums.GroupRole;
import com.hqy.cloud.message.bind.enums.ImMessageState;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.bind.event.support.ReadMessagesEvent;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.db.entity.GroupConversation;
import com.hqy.cloud.message.db.entity.GroupMessage;
import com.hqy.cloud.message.db.entity.PrivateConversation;
import com.hqy.cloud.message.db.entity.PrivateMessage;
import com.hqy.cloud.message.db.service.IGroupConversationService;
import com.hqy.cloud.message.db.service.IGroupMessageService;
import com.hqy.cloud.message.db.service.IPrivateConversationService;
import com.hqy.cloud.message.db.service.IPrivateMessageService;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.service.request.ImChatMessageRequestService;
import com.hqy.cloud.message.service.ImChatMessageService;
import com.hqy.cloud.web.common.UploadResult;
import com.hqy.cloud.web.upload.UploadFileService;
import com.hqy.cloud.web.upload.UploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImChatMessageRequestServiceImpl implements ImChatMessageRequestService {
    private final ImChatMessageService chatMessageService;
    private final IPrivateConversationService privateConversationService;
    private final IPrivateMessageService privateMessageService;
    private final IGroupConversationService groupConversationService;
    private final IGroupMessageService groupMessageService;
    private final UploadFileService uploadFileService;
    private final ImEventListener imEventListener;

    @Override
    public R<PageResult<ImMessageVO>> getMessages(Long id, MessagesRequestParamDTO param) {
        // 根据会话id查找会话
        Long conversationId = param.getConversationId();
        Boolean isGroup = param.getIsGroup();
        PageResult<ImMessageVO> pageResult;
        if (isGroup) {
            // 查询群聊会话
            GroupConversation groupConversation = groupConversationService.getById(conversationId);
            if (groupConversation == null || !groupConversation.getUserId().equals(id)) {
                 return R.failed(ResultCode.ERROR_PARAM);
            }
            // 会话已经被移除
            if (groupConversation.getDeleted()) {
                return R.ok(new PageResult<>());
            }
            if (GroupRole.REMOVED.role.equals(groupConversation.getRole())) {
                // 群聊消息被移除的会话查询走单独的查询
                pageResult = chatMessageService.getRemovedGroupMemberMessages(param.getPage(), param.getLimit(), groupConversation);
            } else {
                pageResult = chatMessageService.getPageMessages(param.getPage(), param.getLimit(), groupConversation.getUserId(), groupConversation.getGroupId(), groupConversation.getLastRemoveTime(), true);
            }
        } else {
            // 查询私聊会话
            PrivateConversation privateConversation = privateConversationService.getById(conversationId);
            if (privateConversation == null || !privateConversation.getUserId().equals(id)) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            // 会话已经被移除
            if (privateConversation.getDeleted()) {
                return R.ok(new PageResult<>());
            }
            pageResult = chatMessageService.getPageMessages(param.getPage(), param.getLimit(), privateConversation.getUserId(), privateConversation.getContactId(), privateConversation.getLastRemoveTime(), false);
        }
        return R.ok(pageResult);
    }


    @Override
    public R<PageResult<ImMessageVO>> searchPageMessages(Long accountId, Long conversationId, Boolean isGroup, MessageType messageType, String keywords, Integer page, Integer limit) {
        // 是否单独查找与某个会话的聊天记录
        boolean queryByConversations = conversationId != null && isGroup != null;
        PageResult<ImMessageVO> pageResult;
        if (queryByConversations) {
            if (isGroup) {
                // 查询群聊会话
                GroupConversation groupConversation = groupConversationService.getById(conversationId);
                if (groupConversation == null || !groupConversation.getUserId().equals(accountId)) {
                    return R.failed(ResultCode.ERROR_PARAM);
                }
                // 会话已经被移除
                if (groupConversation.getDeleted()) {
                    return R.ok(new PageResult<>());
                }
                if (GroupRole.REMOVED.role.equals(groupConversation.getRole())) {
                    pageResult = chatMessageService.searchRemovedGroupMemberMessages(page, limit, groupConversation, messageType, keywords);
                } else {
                    pageResult = chatMessageService.searchPageMessages(page, limit, accountId, groupConversation.getGroupId(), groupConversation.getLastRemoveTime(), messageType, keywords, true);
                }

            } else {
                // 查询私聊会话
                PrivateConversation privateConversation = privateConversationService.getById(conversationId);
                if (privateConversation == null || !privateConversation.getUserId().equals(accountId)) {
                    return R.failed(ResultCode.ERROR_PARAM);
                }
                // 会话已经被移除
                if (privateConversation.getDeleted()) {
                    return R.ok(new PageResult<>());
                }
                pageResult = chatMessageService.searchPageMessages(page, limit, accountId, privateConversation.getContactId(), privateConversation.getLastRemoveTime(), messageType, keywords, false);
            }
        } else {
            // FIXME 接口不会走这个逻辑, 暂时
            pageResult = chatMessageService.searchPageMessages(page, limit, accountId, null, null, messageType, keywords, null);
        }
        return R.ok(pageResult);
    }

    @Override
    public R<ImMessageVO> sendImMessage(Long id, ImMessageDTO message) {
        Boolean isGroup = message.getIsGroup();
        Long toContactId = Long.parseLong(message.getToContactId());
        // 判断是否可以发送聊天消息
        AppsResultCode resultCode = chatMessageService.getEnableChatState(id, toContactId, isGroup);
        if (!resultCode.isSuccess()) {
            return R.failed(resultCode);
        }
        ImMessageVO vo = isGroup ? chatMessageService.sendGroupMessage(id, toContactId, message) : chatMessageService.sendPrivateMessage(id, toContactId, message);
        return R.ok(vo);
    }

    @Override
    public R<ImMessageVO> sendImFileMessage(Long id, MultipartFile file, ImMessageDTO message) {
        Boolean isGroup = message.getIsGroup();
        Long toContactId = Long.parseLong(message.getToContactId());
        // 判断是否可以发送聊天消息
        AppsResultCode resultCode = chatMessageService.getEnableChatState(id, toContactId, isGroup);
        if (!resultCode.isSuccess()) {
            return R.failed(resultCode);
        }
        // 上传文件
        UploadResponse response = uploadFileService.uploadFile(isGroup ? Constants.UPLOAD_IM_GROUP_FOLDER : Constants.UPLOAD_IM_PRIVATE_FOLDER, file);
        UploadResult result = response.getResult();
        if (!result.isResult()) {
            message.setStatus(ImMessageState.FAILED.name);
            return R.ok(message);
        }
        message.setContent(result.getPath());
        message.setFileSize(file.getSize());
        message.setFileName(file.getOriginalFilename());
        ImMessageVO vo = isGroup ? chatMessageService.sendGroupMessage(id, toContactId, message) : chatMessageService.sendPrivateMessage(id, toContactId, message);
        return R.ok(vo);
    }

    @Override
    public R<Boolean> readMessages(Long id, MessageUnreadDTO dto) {
        Boolean isGroup = dto.getIsGroup();
        Long conversationId = dto.getConversationId();
        Long toContactId = dto.getToContactId();
        List<String> readMessages;
        if (isGroup) {
            GroupConversation groupConversation =
                    conversationId == null ? groupConversationService.queryByUserIdAndGroupId(id, toContactId) : groupConversationService.getById(conversationId);
            if (groupConversation == null
                    || !groupConversation.getUserId().equals(id)
                    || !groupConversation.hasUnreadMessage()) {
                return R.ok();
            }
            toContactId = groupConversation.getGroupId();
            readMessages = chatMessageService.readGroupMessages(groupConversation);
        } else {
            PrivateConversation privateConversation =
                    conversationId == null ? privateConversationService.queryByUserIdAndContactId(id, toContactId) : privateConversationService.getById(conversationId);
            if (privateConversation == null || !privateConversation.getUserId().equals(id)) {
                return R.ok();
            }
            readMessages = chatMessageService.readPrivateMessages(id, privateConversation.getContactId());
            toContactId = privateConversation.getContactId();
        }

        if (CollectionUtils.isNotEmpty(readMessages)) {
            ReadMessagesEvent event = new ReadMessagesEvent(isGroup ? id.toString() : toContactId.toString(), readMessages);
            imEventListener.onReadMessages(event);
        }
        return R.ok();
    }

    @Override
    public R<String> undoMessage(Long userId, UndoMessageDTO undoMessage) {
        Long id = undoMessage.getId();
        Boolean isGroup = undoMessage.getIsGroup();
        long currentTimeMillis = System.currentTimeMillis();
        String undoContent;
        if (isGroup) {
            GroupMessage groupMessage = groupMessageService.getById(id);
            // 判断消息是否可以撤回
            if (groupMessage == null || !groupMessage.getSend().equals(id)
                    || currentTimeMillis - groupMessage.getCreated().getTime() > Constants.DEFAULT_UNDO_MESSAGE_TIMESTAMPS) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            undoContent = chatMessageService.undoMessage(true, userId, groupMessage.getGroupId(), id, groupMessage.getMessageId(), groupMessage.getCreated());
        } else {
            PrivateMessage privateMessage = privateMessageService.getById(id);
            // 判断消息是否可以撤回
            if (privateMessage == null || !privateMessage.getSend().equals(userId)
                    || currentTimeMillis - privateMessage.getCreated().getTime() > Constants.DEFAULT_UNDO_MESSAGE_TIMESTAMPS) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            undoContent = chatMessageService.undoMessage(false, userId, privateMessage.getReceive(), id, privateMessage.getMessageId(), privateMessage.getCreated());
        }
        return StringUtils.isNotBlank(undoContent) ? R.ok(undoContent) : R.failed();
    }

    @Override
    public R<List<ImMessageVO>> forwardMessages(Long accountId, ForwardMessageDTO forwardMessage) {
        Long messageId = forwardMessage.getMessageId();
        Boolean isGroup = forwardMessage.getIsGroup();
        String content;
        Integer type;
        if (isGroup) {
            GroupMessage groupMessage = groupMessageService.getById(messageId);
            // 查询转发的消息是否存在.
            if (groupMessage == null || !MessageType.enabledForwardOrSearchEs(groupMessage.getType())) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            content = groupMessage.getContent();
            type = groupMessage.getType();
        } else {
            // 查询转发的消息是否存在.
            PrivateMessage privateMessage = privateMessageService.getById(messageId);
            if (privateMessage == null || !MessageType.enabledForwardOrSearchEs(privateMessage.getType())) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            content = privateMessage.getContent();
            type = privateMessage.getType();
        }
        return R.ok(chatMessageService.forwardMessage(accountId, content, type, forwardMessage));
    }
}
