package com.hqy.cloud.message.server;

import com.hqy.cloud.message.bind.dto.ChatMessageDTO;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.message.bind.enums.ImMessageState;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.db.entity.PrivateConversation;
import com.hqy.cloud.message.db.entity.PrivateMessage;
import com.hqy.cloud.message.es.document.ImMessage;
import com.hqy.cloud.message.server.support.convertor.MessageConvertorHolder;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/27
 */
public class MessageFactory {

    public static ImMessageVO create(boolean group, Long loginUser, ChatMessageDTO message, UserInfoVO sender, UserInfoVO receiver) {
        ImMessageVO vo = ImMessageVO.builder().id(message.getMessageId())
                .messageId(message.getId().toString())
                .fromUser(sender)
                .contactUser(receiver)
                .toContactId(message.getToContactId().toString())
                .isGroup(group)
                .status(ImMessageState.getState(message.getStatus()))
                .type(MessageType.getMessageType(message.getType()))
                .messageType(message.getType())
                .isRead(message.getIsRead())
                .content(message.getContent())
                .sendTime(message.getCreated().getTime()).build();

        // 获取消息转换器
        MessageConvertor convertor = MessageConvertorHolder.getConvertor(message.getType());
        return convertor.process(loginUser, vo);
    }

    public static ImMessageVO create(ImMessage message, UserInfoVO sender) {
        return ImMessageVO.builder()
                .id(message.getMessageId())
                .messageId(message.getDbId().toString())
                .fromUser(sender)
                .toContactId(message.getReceive().toString())
                .isGroup(message.getIsGroup())
                .status(ImMessageState.SUCCESS.name)
                .type(MessageType.getMessageType(message.getType()))
                .messageType(message.getType())
                .sendTime(message.getSend())
                .fileName(message.getFileName())
                .fileSize(message.getFileSize())
                .content(message.getContent()).build();
    }

    public static ImMessageVO create(PrivateMessage privateMessage, UserInfoVO sender) {
        ImMessageDTO message = new ImMessageDTO(privateMessage, sender);
        // 获取消息转换器
        MessageConvertor convertor = MessageConvertorHolder.getConvertor(privateMessage.getType());
        return convertor.process(privateMessage.getSend(), message);
    }

    public static ImMessageDTO create(boolean group, Long loginUser, UserInfoVO sender, Long messageId, Long contactId, Integer messageType, String typeName, String content, Date created) {
        ImMessageDTO messageDTO = new ImMessageDTO(messageId, group, sender, content, contactId, typeName);
        messageDTO.setMessageType(messageType);
        messageDTO.setSendTime(created == null ? System.currentTimeMillis() : created.getTime());
        // 获取消息转换器
        MessageConvertor convertor = MessageConvertorHolder.getConvertor(messageType);
        return (ImMessageDTO) convertor.process(loginUser, messageDTO);
    }

    public static ConversationVO create(Long loginId, ConversationDTO conversation) {
        Integer lastMessageType = conversation.getLastMessageType();
        Boolean isGroup = conversation.getIsGroup();
        ConversationVO vo = ConversationVO.builder()
                .id(conversation.getContactId().toString())
                .conversationId(conversation.getId().toString())
                .type(MessageType.getMessageType(lastMessageType))
                .displayName(conversation.getDisplayName())
                .lastSendTime(conversation.getLastMessageTime() == null ? null : conversation.getLastMessageTime())
                .isNotice(conversation.getIsNotice())
                .isTop(conversation.getIsTop())
                .avatar(conversation.getAvatar())
                .unread(conversation.getUnread() == null ? 0 : conversation.getUnread()).build();
        if (isGroup) {
            vo.setIsGroup(true);
            vo.setCreator(conversation.getCreator().toString());
            vo.setRole(conversation.getRole());
            vo.setNotice(conversation.getNotice());
        } else {
            vo.setIsGroup(false);
        }

        if (EventMessageType.UNDO.type.equals(lastMessageType)) {
            vo.setType(MessageType.TEXT.name);
        }

        if (lastMessageType != null) {
            // 获取消息转换器
            MessageConvertor convertor = MessageConvertorHolder.getConvertor(lastMessageType);
            vo.setLastContent(convertor.processByConversation(loginId, conversation.getLastMessageContent(), isGroup));
        }
        return vo;
    }

    public static ConversationVO create(int unread, UserInfoVO contactInfo, PrivateConversation conversation) {
        Integer lastMessageType = conversation.getLastMessageType();
        ConversationVO conversationVO = ConversationVO.builder()
                .id(conversation.getContactId().toString())
                .conversationId(conversation.getId().toString())
                .displayName(contactInfo.getDisplayName())
                .avatar(contactInfo.getAvatar())
                .isGroup(false)
                .unread(unread)
                .isNotice(conversation.getNotice())
                .isTop(conversation.getTop())
                .type(MessageType.getMessageType(lastMessageType))
                .lastSendTime(conversation.getLastMessageTime()).build();

        if (EventMessageType.UNDO.type.equals(lastMessageType)) {
            conversationVO.setType(MessageType.TEXT.name);
        }

        // 获取消息转换器
        MessageConvertor convertor = MessageConvertorHolder.getConvertor(lastMessageType);
        conversationVO.setLastContent(convertor.processByConversation(conversation.getUserId(), conversation.getLastMessageContent(), false));
        return conversationVO;
    }







}
