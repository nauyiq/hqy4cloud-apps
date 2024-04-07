package com.hqy.cloud.message.db.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.db.mybatisplus.BasePlusServiceImpl;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.UnreadDTO;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.message.bind.enums.GroupRole;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.bind.event.support.AppendChatEvent;
import com.hqy.cloud.message.bind.vo.ContactVO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.db.entity.GroupConversation;
import com.hqy.cloud.message.db.mapper.GroupConversationMapper;
import com.hqy.cloud.message.db.service.IGroupConversationService;
import com.hqy.cloud.message.server.ImEventListener;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 群聊聊天会话表 服务实现类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-06
 */
@Service
@RequiredArgsConstructor
public class GroupConversationServiceImpl extends BasePlusServiceImpl<GroupConversation, GroupConversationMapper> implements IGroupConversationService {
    private final ImEventListener imEventListener;

    @Override
    public boolean updateGroupConversations(Set<Long> groupMembers, Long groupId, Integer messageType, ImMessageDTO message) {
        return baseMapper.updateGroupConversations(groupMembers, groupId, message.getContent(), messageType, SystemClock.now()) > 0;
    }

    @Override
    public List<ConversationDTO> queryConversationsByUserId(Long userId) {
        List<ConversationDTO> conversations = baseMapper.queryConversationsByUserId(userId);
        if (CollectionUtils.isEmpty(conversations)) {
            return new ArrayList<>();
        }
        conversations.forEach(conversation -> {
            conversation.setIsGroup(true);
            Long lastReadTime = conversation.getLastReadTime();
            if (lastReadTime != null) {
                conversation.setLastReadDate(new Date(lastReadTime));
            }
            if (GroupRole.REMOVED.role.equals(conversation.getRole())) {
                conversation.setNotice(StrUtil.EMPTY);
            }
        });
        // 获取需要查询群聊未读消息的会话
        List<ConversationDTO> needQueryUnreadConversations = conversations.stream().filter(conversation -> {
            Long lastReadTime = conversation.getLastReadTime();
            Long lastMessageTime = conversation.getLastMessageTime();
            if (lastMessageTime == null) {
                return false;
            }
            if (lastReadTime == null) {
                return true;
            }
            // 最后一次消息时间大于最后一次读取消息时间 说明当前群聊存在未读消息
            return lastMessageTime > lastReadTime;
        }).toList();

        if(CollectionUtils.isEmpty(needQueryUnreadConversations)) {
            return conversations;
        }
        // 未读消息MAP
        Map<Long, Integer> unreadMap = baseMapper.queryGroupUnreadByConversations(userId, needQueryUnreadConversations)
                .stream().collect(Collectors.toMap(UnreadDTO::getGroupId, UnreadDTO::getUnread));
        conversations.forEach(conversation -> {
            Long groupId = conversation.getContactId();
            conversation.setUnread(unreadMap.getOrDefault(groupId, 0));
        });
        return conversations;
    }

    @Override
    public ConversationDTO queryConversationInfoByUserIdAndGroupId(Long id, Long groupId) {
        return baseMapper.queryConversationInfoByUserIdAndGroupId(id, groupId);
    }

    @Override
    public GroupConversation queryByUserIdAndGroupId(Long userId, Long groupId) {
        List<GroupConversation> list = query()
                .eq("user_id", userId)
                .eq("group_id", groupId)
                .eq("deleted", 0).list();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public boolean updateConversationUndoMessage(Long userId, Long groupId, String content) {
        // 群聊所有会话修改
        return baseMapper.updateConversationTypeAndContent(null, groupId, content, EventMessageType.UNDO.type) > 0;
    }

    @Override
    public void sendAppendGroupChatEvent(List<GroupConversation> groupConversations, String groupAvatar, String groupName, Long groupCreator) {
        List<AppendChatEvent> events = new ArrayList<>();
        for (GroupConversation groupConversation : groupConversations) {
            Long groupId = groupConversation.getGroupId();
            Long userId = groupConversation.getUserId();
            Integer messageType = groupConversation.getLastMessageType();
            Long lastMessageTime = groupConversation.getLastMessageTime();
            // 会话对象
            ConversationVO conversationVO = ConversationVO.builder()
                    .id(groupId.toString())
                    .conversationId(groupConversation.getId().toString())
                    .displayName(groupName)
                    .avatar(groupAvatar)
                    .isGroup(true)
                    .creator(groupCreator.toString())
                    .unread(0)
                    .isNotice(groupConversation.getNotice())
                    .isTop(groupConversation.getTop())
                    .lastContent(groupConversation.getLastMessageContent())
                    .type(messageType == null ? null : MessageType.getMessageType(messageType))
                    .role(groupConversation.getRole())
                    .lastSendTime(lastMessageTime == null ? SystemClock.now() : lastMessageTime).build();
            // 联系人对象
            ContactVO contactVO = ContactVO.builder()
                    .id(groupId.toString())
                    .displayName(groupName)
                    .avatar(groupAvatar)
                    .creator(groupCreator.toString())
                    .isTop(groupConversation.getTop())
                    .isNotice(groupConversation.getNotice())
                    .isGroup(true).build();
            AppendChatEvent chatEvent = AppendChatEvent.of(userId.toString(), conversationVO, contactVO);
            events.add(chatEvent);
        }
        imEventListener.onImAppendGroupChatEvent(events);
    }

    @Override
    public boolean insertOrUpdate(List<GroupConversation> conversations) {
        return baseMapper.insertOrUpdate(conversations) > 0;
    }

    @Override
    public List<GroupConversation> insertOrUpdateReturnConversations(List<GroupConversation> conversations) {
        if (baseMapper.insertOrUpdate(conversations) > 0) {
            List<Long> userIds = conversations.stream().map(GroupConversation::getUserId).toList();
            return baseMapper.queryConversationsByGroupIdAndUserIds(conversations.get(0).getGroupId(), userIds);
        }
        return null;
    }

    @Override
    public boolean updateGroupConversationRoleAndUpdated(Long groupId, Long userId, Integer role, Date updated) {
        return baseMapper.updateGroupConversationRoleAndUpdated(groupId, userId, role, updated) > 0;
    }

    @Override
    public boolean removeGroupConversation(Long groupId, Long userId) {
        return baseMapper.removeGroupConversation(groupId, userId) > 0;
    }

    @Override
    public boolean realRemoveGroupConversation(Long groupId, Long userId) {
        try {
            // 有可能数据已经删除，因此不存在也认为删除成功
            baseMapper.realRemoveGroupConversation(groupId, userId);
            return true;
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }
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
