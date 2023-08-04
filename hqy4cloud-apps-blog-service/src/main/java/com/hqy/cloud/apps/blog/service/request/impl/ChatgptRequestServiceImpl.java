package com.hqy.cloud.apps.blog.service.request.impl;

import com.hqy.cloud.apps.blog.entity.ChatgptConversation;
import com.hqy.cloud.apps.blog.entity.ChatgptMessageHistory;
import com.hqy.cloud.apps.blog.entity.ChatgptRole;
import com.hqy.cloud.apps.blog.service.opeations.ChatgptOperationService;
import com.hqy.cloud.apps.blog.service.request.ChatgptRequestService;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptConversationVO;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptMessageHistoryVO;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptRoleVO;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptSystemConfigVO;
import com.hqy.cloud.common.bind.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:41
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatgptRequestServiceImpl implements ChatgptRequestService {
    private final ChatgptOperationService operationService;

    @Override
    public R<List<ChatgptRoleVO>> getEnableChatgptRoles() {
        List<ChatgptRole> roles = operationService.getChatgptRolesByStatus(true);
        List<ChatgptRoleVO> vos;
        if (CollectionUtils.isEmpty(roles)) {
            vos = Collections.emptyList();
        } else {
            vos = roles.parallelStream()
                    .map(ChatgptRoleVO::new).collect(Collectors.toList());
        }
        return R.ok(vos);
    }

    @Override
    public R<List<ChatgptConversationVO>> getChatgptConversations(Long id) {
        List<ChatgptConversation> conversations = operationService.getChatgptConversationsByUserId(id);
        List<ChatgptConversationVO> vos;
        if (CollectionUtils.isEmpty(conversations)) {
            vos = Collections.emptyList();
        } else {
            vos = conversations.parallelStream()
                    .map(ChatgptConversationVO::new).collect(Collectors.toList());
        }
        return R.ok(vos);
    }

    @Override
    public R<Boolean> updateChatgptConversation(ChatgptConversation conversation) {
         boolean result = operationService.updateChatgptConversations(conversation);
         return result ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> deleteChatgptConversation(String chatId, Long userId) {
        boolean result = operationService.deleteChatgptConversation(chatId, userId);
        return result ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> clearChatgptConversations(Long userId) {
        boolean result = operationService.clearChatgptConversations(userId);
        return result ? R.ok() : R.failed();
    }

    @Override
    public R<List<ChatgptMessageHistoryVO>> getChatgptMessages(String chatId, Long id) {
        List<ChatgptMessageHistory> histories = operationService.getChatgptConversationMessageHistory(chatId, id);
        List<ChatgptMessageHistoryVO> vos;
        if (CollectionUtils.isEmpty(histories)) {
            vos = Collections.emptyList();
        } else {
            vos = histories.parallelStream()
                    .map(ChatgptMessageHistoryVO::new).collect(Collectors.toList());
        }
        return R.ok(vos);
    }

    @Override
    public R<ChatgptSystemConfigVO> getChatgptSystemConfig() {
        ChatgptSystemConfigVO vo = operationService.getChatgptSystemConfig();
        vo = Objects.isNull(vo) ? ChatgptSystemConfigVO.DEFAULT : vo;
        return R.ok(vo);
    }
}
