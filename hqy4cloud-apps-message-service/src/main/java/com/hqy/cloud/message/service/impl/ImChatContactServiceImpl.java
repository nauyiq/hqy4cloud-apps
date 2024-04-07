package com.hqy.cloud.message.service.impl;

import com.hqy.cloud.message.bind.dto.ContactDTO;
import com.hqy.cloud.message.bind.dto.ContactsDTO;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.bind.event.support.ImNoticeChatEvent;
import com.hqy.cloud.message.bind.event.support.ImTopChatEvent;
import com.hqy.cloud.message.db.service.IFriendStateService;
import com.hqy.cloud.message.db.service.IGroupConversationService;
import com.hqy.cloud.message.db.service.IGroupMemberService;
import com.hqy.cloud.message.db.service.IPrivateConversationService;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.service.ImChatContactService;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/6
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImChatContactServiceImpl implements ImChatContactService {
    private final IGroupConversationService groupConversationService;
    private final IGroupMemberService groupMemberService;

    private final IPrivateConversationService privateConversationService;
    private final IFriendStateService friendStateService;

    private final ImEventListener imEventListener;
    private final TransactionTemplate template;

    @Override
    public List<ConversationDTO> getUserConversations(Long userId) {
        // 查询私人聊天会话
        List<ConversationDTO> conversations = privateConversationService.queryConversationsByUserId(userId);
        // 查询群聊聊天会话
        List<ConversationDTO> groupConversations = groupConversationService.queryConversationsByUserId(userId);
        conversations.addAll(groupConversations);
        return conversations;
    }

    @Override
    public ContactsDTO getUserContacts(Long userId) {
        ContactsDTO contacts = friendStateService.queryContactsByUserId(userId);
        List<ContactDTO> groupContacts = groupMemberService.queryContactsByUserId(userId);
        if (CollectionUtils.isNotEmpty(groupContacts)) {
            // 群聊联系人添加到联系人列表中.
            List<ContactDTO> privateContacts = contacts.getContacts();
            privateContacts.addAll(groupContacts);
        }
        return contacts;
    }

    @Override
    public boolean updateChatTopState(Boolean isGroup, Long userId, Long contactId, Long conversationId, Boolean status) {
        Boolean execute = template.execute(s -> {
            try {
                if (isGroup) {
                    AssertUtil.isTrue(groupConversationService.updateConversationTopState(conversationId, status), "Failed execute to update group conversation top state.");
                    AssertUtil.isTrue(groupMemberService.updateTopState(userId, contactId, status), "Failed execute to update group top state.");
                } else {
                    AssertUtil.isTrue(privateConversationService.updateConversationTopState(conversationId, status), "Failed execute to update private conversation top state.");
                    AssertUtil.isTrue(friendStateService.updateTopState(userId, contactId, status), "Failed execute to update friend top state.");
                }
                return true;
            } catch (Throwable cause) {
                s.setRollbackOnly();
                return false;
            }
        });

        if (Boolean.TRUE.equals(execute)) {
            ImTopChatEvent chatEvent = ImTopChatEvent.of(userId.toString(), contactId.toString(), conversationId.toString(), status);
            imEventListener.onImTopChatEvent(chatEvent);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateChatNoticeState(Boolean isGroup, Long userId, Long contactId, Long conversationId, Boolean status) {
        Boolean execute = template.execute(s -> {
            try {
                if (isGroup) {
                    AssertUtil.isTrue(groupConversationService.updateConversationNoticeState(conversationId, status), "Failed execute to update group conversation notice state.");
                    AssertUtil.isTrue(groupMemberService.updateNoticeState(userId, contactId, status), "Failed execute to update group notice state.");
                } else {
                    AssertUtil.isTrue(privateConversationService.updateConversationNoticeState(conversationId, status), "Failed execute to update private conversation notice state.");
                    AssertUtil.isTrue(friendStateService.updateNoticeState(userId, contactId, status), "Failed execute to update friend notice state.");
                }
                return true;
            } catch (Throwable cause) {
                s.setRollbackOnly();
                return false;
            }
        });

        if (Boolean.TRUE.equals(execute)) {
            ImNoticeChatEvent event = ImNoticeChatEvent.of(userId.toString(), contactId.toString(), conversationId.toString(), status);
            imEventListener.onImNoticeChatEvent(event);
            return true;
        }
        return false;
    }
}
