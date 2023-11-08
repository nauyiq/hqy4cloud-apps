package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.message.bind.dto.ChatDTO;
import com.hqy.cloud.message.bind.dto.ForwardMessageDTO;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.mapper.ImConversationMapper;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 13:24
 */
@Service
@RequiredArgsConstructor
public class ImConversationTkServiceImpl extends BaseTkServiceImpl<ImConversation, Long> implements ImConversationTkService {
    private final ImConversationMapper mapper;

    @Override
    public BaseTkMapper<ImConversation, Long> getTkMapper() {
        return mapper;
    }

    @Override
    public boolean insertOrUpdate(List<ImConversation> imConversations) {
        return mapper.insertOrUpdate(imConversations) > 0;
    }

    @Override
    public List<ImConversation> queryGroupConversationMembers(Long groupId) {
        return mapper.queryGroupConversationMembers(groupId);
    }

    @Override
    public List<ImConversation> queryConversations(Long id, Long contactId, Boolean isGroup) {
        if (isGroup != null && isGroup) {
            return mapper.select(ImConversation.of(null, contactId, true)).stream().filter(conversation -> conversation.getDeleted() == null).toList();
        } else {
            return mapper.queryPrivateConversations(id, contactId);
        }
    }

    @Override
    public List<ChatDTO> queryImChatDTO(Long userId) {
        return mapper.queryImChatDTO(userId);
    }

    @Override
    public void removeConversations(List<ImConversation> imConversations) {
        mapper.removeConversations(imConversations);
    }

    @Override
    public boolean deleteConversation(Long userId, Long contactId, boolean isGroup, Long removeTime) {
        return mapper.deleteConversation(userId, contactId, isGroup ? 1 : 0, removeTime) > 0;
    }

    @Override
    public void undoConversations(List<ImConversation> conversations) {
        mapper.undoConversations(conversations);
    }

    @Override
    public List<ImConversation> queryConversationsByForwards(Long id, List<ForwardMessageDTO.Forward> conversationForwards) {
        return mapper.queryConversationsByForwards(id, conversationForwards);
    }
}
