package com.hqy.cloud.message.db.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.hqy.cloud.db.mybatisplus.BasePlusServiceImpl;
import com.hqy.cloud.message.bind.Constants;
import com.hqy.cloud.message.bind.ConvertUtil;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.ImUserInfoDTO;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.bind.event.support.AppendChatEvent;
import com.hqy.cloud.message.bind.vo.ContactVO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.db.entity.PrivateConversation;
import com.hqy.cloud.message.db.entity.PrivateMessage;
import com.hqy.cloud.message.db.mapper.PrivateConversationMapper;
import com.hqy.cloud.message.db.service.IPrivateConversationService;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.server.MessageFactory;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 私聊聊天会话表 服务实现类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-06
 */
@Service
@RequiredArgsConstructor
public class PrivateConversationServiceImpl extends BasePlusServiceImpl<PrivateConversation, PrivateConversationMapper> implements IPrivateConversationService {
    private final ImEventListener imEventListener;

    @Override
    public List<ConversationDTO> queryConversationsByUserId(Long userId) {
        List<ConversationDTO> conversations = baseMapper.queryConversationInfoAndMessageUnreadCountByUserId(userId);
        if (CollectionUtils.isEmpty(conversations)) {
            return new ArrayList<>();
        }
        conversations.forEach(conversation -> conversation.setIsGroup(false));
        return conversations;
    }

    @Override
    public ConversationDTO queryConversationInfoByUserIdAndFriendId(Long userId, Long friendId) {
        return baseMapper.queryConversationInfoByUserIdAndFriendId(userId, friendId);
    }

    @Override
    public Long insertOrUpdateConversationAndReturnConversationId(PrivateConversation conversation, PrivateConversation iConversation) {
        int i = baseMapper.insertOrUpdate(List.of(conversation, iConversation));
        if (i > 0) {
            return baseMapper.queryIdByUniqueIndex(conversation.getUserId(), conversation.getContactId());
        }
        return null;
    }

    @Override
    public boolean insertOrUpdate(List<PrivateConversation> conversations) {
        return baseMapper.insertOrUpdate(conversations) > 0;
    }

    @Override
    public void sendAppendPrivateChatEvent(PrivateConversation conversation, UserInfoVO fromUser) {
        // 构建会话vo对象
        ConversationVO conversationVO = MessageFactory.create(0, fromUser, conversation);

        // 构建联系人vo对象
        ContactVO contactVO = ContactVO.builder()
                .id(conversation.getContactId().toString())
                .index(ConvertUtil.getIndex(false, fromUser.getDisplayName()))
                .displayName(fromUser.getDisplayName())
                .avatar(fromUser.getAvatar())
                .isTop(conversation.getTop())
                .isNotice(conversation.getNotice()).build();

        AppendChatEvent chatEvent = AppendChatEvent.of(conversation.getUserId().toString(), conversationVO, contactVO);
        imEventListener.onImAppendPrivateChatEvent(chatEvent);
    }

    @Override
    public List<PrivateConversation> insertOrUpdateAddFriendConversations(Long userId, Long friendId, PrivateMessage message, Map<Long, ImUserInfoDTO> friendInfoMap) {
        List<PrivateConversation> conversations = PrivateConversation.ofList(userId, friendId, message, friendInfoMap);
        if (baseMapper.insertOrUpdate(conversations) > 0) {
            // 重新查询会话, 主要查询会话id
            conversations = baseMapper.queryConversationsByUserIdAndFriendId(userId, friendId);
            return conversations;
        }
        throw new RuntimeException("Failed execute to Insert or update.");
    }

    @Override
    public boolean removeConversation(Long userId, Long friendId) {
        return baseMapper.removeConversation(userId, friendId) > 0;
    }

    @Override
    public boolean updateConversationDisplayName(Long userId, Long contactId, String displayName) {
        return baseMapper.updateConversationDisplayName(userId, contactId, displayName) > 0;
    }

    @Override
    public PrivateConversation queryByUserIdAndContactId(Long id, Long toContactId) {
        List<PrivateConversation> list = query().eq("user_id", id)
                .eq("contact_id", toContactId)
                .eq("deleted", 0).list();
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public boolean updateConversationUndoMessage(Long userId, Long contactId, String content) {
        List<PrivateConversation> privateConversations = List.of(
                PrivateConversation.builder().userId(userId).contactId(contactId).lastMessageType(EventMessageType.UNDO.type).lastMessageContent(content).build(),
                PrivateConversation.builder().userId(contactId).contactId(userId).lastMessageType(EventMessageType.UNDO.type).lastMessageContent(content).build()
        );
        return baseMapper.duplicateUpdateTypeAndContent(privateConversations) > 0;
    }

    @Override
    public boolean updateConversationTopState(Long conversationId, Boolean status) {
        return baseMapper.updateConversationTopState(conversationId, status) > 0;
    }

    @Override
    public boolean updateConversationNoticeState(Long conversationId, Boolean status) {
        return baseMapper.updateConversationNoticeState(conversationId, status) > 0;
    }
}
