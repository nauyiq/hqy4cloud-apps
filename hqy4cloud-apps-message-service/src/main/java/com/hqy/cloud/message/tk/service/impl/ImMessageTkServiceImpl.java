package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.tk.entity.ImMessage;
import com.hqy.cloud.message.tk.mapper.ImMessageMapper;
import com.hqy.cloud.message.tk.service.ImMessageTkService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:37
 */
@Service
@RequiredArgsConstructor
public class ImMessageTkServiceImpl extends BaseTkServiceImpl<ImMessage, Long> implements ImMessageTkService {
    private final ImMessageMapper mapper;

    @Override
    public BaseTkMapper<ImMessage, Long> getTkMapper() {
        return mapper;
    }

    @Override
    public List<MessageUnreadDTO> queryUnread(Long id, List<MessageUnreadDTO> messageUnreadList) {
        if (CollectionUtils.isEmpty(messageUnreadList)) {
            return Collections.emptyList();
        }
        Map<Boolean, List<MessageUnreadDTO>> map = messageUnreadList.parallelStream().collect(Collectors.groupingBy(MessageUnreadDTO::getIsGroup));
        List<MessageUnreadDTO> privateConversations = map.getOrDefault(Boolean.FALSE, new ArrayList<>());
        if (CollectionUtils.isNotEmpty(privateConversations)) {
            List<Long> fromIds = privateConversations.parallelStream().map(MessageUnreadDTO::getFrom).toList();
            Map<Long, Integer> result = mapper.getMessageUnread(id, fromIds).parallelStream().collect(Collectors.toMap(MessageUnreadDTO::getFrom, MessageUnreadDTO::getUnread));
            privateConversations = privateConversations.stream()
                    .peek(conversation -> conversation.setUnread(result.getOrDefault(conversation.getFrom(), 0))).collect(Collectors.toList());

        }
        List<MessageUnreadDTO> groupConversations = map.getOrDefault(Boolean.TRUE, new ArrayList<>());
        groupConversations.addAll(privateConversations);
        return groupConversations;
    }

    @Override
    public boolean updateMessagesRead(List<Long> unreadMessageIds) {
        return mapper.updateMessagesRead(unreadMessageIds) > 0;
    }
}
