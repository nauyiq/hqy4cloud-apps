package com.hqy.cloud.message.service.request.impl;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.ImChatConfigDTO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.service.ImConversationOperationsService;
import com.hqy.cloud.message.service.request.ImConversationRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 16:40
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImConversationRequestServiceImpl implements ImConversationRequestService {
    private final ImConversationOperationsService imConversationOperationsService;

    @Override
    public R<List<ConversationVO>> getConversations(Long id) {
        return R.ok(imConversationOperationsService.getImConversations(id));
    }

    @Override
    public R<Boolean> updateChatTop(Long id, ImChatConfigDTO chatConfig) {
        Boolean isGroup = chatConfig.getIsGroup();
        boolean result = isGroup ? imConversationOperationsService.updateGroupChatTopStatus(id, chatConfig.getContactId(), chatConfig.getStatus())
                : imConversationOperationsService.updatePrivateChatTopStatus(id, chatConfig.getContactId(), chatConfig.getStatus());
        return result ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> updateChatNotice(Long id, ImChatConfigDTO chatConfig) {
        Boolean isGroup = chatConfig.getIsGroup();
        boolean result = isGroup ? imConversationOperationsService.updateGroupChatNoticeStatus(id, chatConfig.getContactId(), chatConfig.getStatus())
                : imConversationOperationsService.updatePrivateChatNoticeStatus(id, chatConfig.getContactId(), chatConfig.getStatus());
        return result ? R.ok() : R.failed();
    }




}
