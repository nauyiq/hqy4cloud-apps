package com.hqy.cloud.message.service.impl;

import cn.hutool.core.date.SystemClock;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.dto.ContactDTO;
import com.hqy.cloud.message.bind.dto.ContactsDTO;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.bind.dto.ImChatConfigDTO;
import com.hqy.cloud.message.bind.vo.ContactVO;
import com.hqy.cloud.message.bind.vo.ContactsVO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.db.entity.GroupConversation;
import com.hqy.cloud.message.db.entity.PrivateConversation;
import com.hqy.cloud.message.db.service.IGroupConversationService;
import com.hqy.cloud.message.db.service.IPrivateConversationService;
import com.hqy.cloud.message.server.MessageFactory;
import com.hqy.cloud.message.service.ImChatContactService;
import com.hqy.cloud.message.service.request.ImChatConversationRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/6
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImChatConversationRequestServiceImpl implements ImChatConversationRequestService {
    private final ImChatContactService imChatContactService;
    private final IPrivateConversationService privateConversationService;
    private final IGroupConversationService groupConversationService;

    @Override
    public R<List<ConversationVO>> getImUserConversations(Long userId) {
        // 搜索用户聊天列表
        List<ConversationDTO> conversations = imChatContactService.getUserConversations(userId);
        if (CollectionUtils.isEmpty(conversations)) {
            return R.ok(new ArrayList<>());
        }
        List<ConversationVO> vos = conversations.stream()
                .filter(Objects::nonNull)
                .map(conversation -> MessageFactory.create(userId, conversation)).toList();
        return R.ok(vos);
    }

    @Override
    public R<ContactsVO> getImUserContacts(Long userId) {
        // 搜索用户联系人, 朋友 + 群聊
        ContactsDTO contactsDTO = imChatContactService.getUserContacts(userId);
        if (contactsDTO == null) {
            return R.ok(ContactsVO.of());
        }
        List<ContactDTO> contacts = contactsDTO.getContacts();
        Integer applicationUnread = contactsDTO.getApplicationUnread();
        if (CollectionUtils.isEmpty(contacts)) {
            return R.ok(ContactsVO.of(applicationUnread, Collections.emptyList()));
        }
        List<ContactVO> vos = contacts.stream().map(ContactVO::of).toList();
        return R.ok(ContactsVO.of(applicationUnread, vos));
    }


    @Override
    public R<Boolean> updateChatTopState(Long id, ImChatConfigDTO chatConfig) {
        Boolean isGroup = chatConfig.getIsGroup();
        Long contactId = chatConfig.getContactId();
        Long conversationId;
        if (isGroup) {
            GroupConversation conversation = groupConversationService.queryByUserIdAndGroupId(id, contactId);
            if (conversation == null) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            if (chatConfig.getStatus().equals(conversation.getTop())) {
                return R.ok();
            }
            conversationId = conversation.getId();
        } else {
            PrivateConversation conversation = privateConversationService.queryByUserIdAndContactId(id, contactId);
            if (conversation == null) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            if (chatConfig.getStatus().equals(conversation.getTop())) {
                return R.ok();
            }
            conversationId = conversation.getId();
        }
        return imChatContactService.updateChatTopState(isGroup, id, contactId, conversationId, chatConfig.getStatus()) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> updateChatNoticeState(Long id, ImChatConfigDTO chatConfig) {
        Boolean isGroup = chatConfig.getIsGroup();
        Long contactId = chatConfig.getContactId();
        Long conversationId;
        if (isGroup) {
            GroupConversation conversation = groupConversationService.queryByUserIdAndGroupId(id, contactId);
            if (conversation == null) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            if (chatConfig.getStatus().equals(conversation.getNotice())) {
                return R.ok();
            }
            conversationId = conversation.getId();
        } else {
            PrivateConversation conversation = privateConversationService.queryByUserIdAndContactId(id, contactId);
            if (conversation == null) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            if (chatConfig.getStatus().equals(conversation.getNotice())) {
                return R.ok();
            }
            conversationId = conversation.getId();
        }
        return imChatContactService.updateChatNoticeState(isGroup, id, contactId, conversationId, chatConfig.getStatus()) ? R.ok() : R.failed();
    }

    @Override
    public R<ConversationVO> addConversation(Long id, Long contactId, Boolean isGroup) {
        ConversationDTO conversationDTO;
        if (isGroup) {
            conversationDTO = groupConversationService.queryConversationInfoByUserIdAndGroupId(id, contactId);
            if (conversationDTO == null || conversationDTO.getContactId() == null) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            // 构造群聊会话对象
            GroupConversation groupConversation = GroupConversation.of(id, conversationDTO);
            boolean result = groupConversation.getId() == null ? groupConversationService.save(groupConversation) : groupConversationService.updateById(groupConversation);
            if (!result) {
                return R.failed();
            }
        } else {
            conversationDTO = privateConversationService.queryConversationInfoByUserIdAndFriendId(id, contactId);
            if (conversationDTO == null || conversationDTO.getContactId() == null) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            // 构造私聊会话对象
            PrivateConversation privateConversation = PrivateConversation.of(id, conversationDTO);
            boolean result = privateConversation.getId() == null ? privateConversationService.save(privateConversation) : privateConversationService.updateById(privateConversation);
            if (!result) {
                return R.failed();
            }
        }
        conversationDTO.setIsGroup(isGroup);
        ConversationVO vo = ConversationVO.of(conversationDTO);
        return R.ok(vo);
    }

    @Override
    public R<Boolean> deleteConversation(Long userId, boolean group, Long conversationId) {
        boolean result;
        if (group) {
            GroupConversation groupConversation = groupConversationService.getById(conversationId);
            if (groupConversation == null || !userId.equals(groupConversation.getUserId())) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            groupConversation.setDeleted(true);
            groupConversation.setLastRemoveTime(SystemClock.now());
            result = groupConversationService.updateById(groupConversation);
        } else {
            PrivateConversation privateConversation = privateConversationService.getById(conversationId);
            if (privateConversation == null || !userId.equals(privateConversation.getUserId())) {
                return R.failed(ResultCode.ERROR_PARAM);
            }
            privateConversation.setDeleted(true);
            privateConversation.setLastRemoveTime(SystemClock.now());
            result = privateConversationService.updateById(privateConversation);
        }
        return result ? R.ok() : R.failed();
    }
}
