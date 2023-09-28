package com.hqy.cloud.message.service.request.impl;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.dto.ImChatConfigDTO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.bind.vo.ImChatVO;
import com.hqy.cloud.message.service.ImConversationOperationsService;
import com.hqy.cloud.message.service.ImFriendOperationsService;
import com.hqy.cloud.message.service.request.ImConversationRequestService;
import com.hqy.cloud.message.tk.entity.ImUserSetting;
import com.hqy.cloud.message.tk.service.ImUserSettingTkService;
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
    private final ImUserSettingTkService userSettingTkService;
    private final ImFriendOperationsService friendOperationsService;
    private final ImConversationOperationsService imConversationOperationsService;

    @Override
    public R<List<ConversationVO>> getConversations(Long id) {
        return R.ok(imConversationOperationsService.getImConversations(id));
    }

    @Override
    public R<ImChatVO> getImChatInfo(Long userId) {
        return R.ok(imConversationOperationsService.getImChatInfoList(userId));
    }

    @Override
    public R<ConversationVO> addConversation(Long id, Long userId) {
        if (!friendOperationsService.isFriend(id, userId)) {
            // 不是好友情况下查询该用户是否允许陌生人聊天
            ImUserSetting imUserSetting = userSettingTkService.queryById(userId);
            if (imUserSetting == null || !imUserSetting.getPrivateChat()) {
                return R.failed(ResultCode.NOT_PERMISSION);
            }
        }
        ConversationVO vo = imConversationOperationsService.addConversation(id, userId);
        return vo == null ? R.failed(ResultCode.SYSTEM_BUSY) : R.ok(vo);
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

    @Override
    public R<Boolean> deleteConversation(Long userId, Long conversationId) {
        /*ImConversation conversation = imConversationTkService.queryById(conversationId);
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            return R.failed(ResultCode.ERROR_PARAM);
        }*/

        return null;
    }
}
