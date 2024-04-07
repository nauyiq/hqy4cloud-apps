package com.hqy.cloud.message.service;

import com.hqy.cloud.message.bind.dto.ContactsDTO;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.bind.vo.ImChatVO;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/6
 */
public interface ImChatContactService {

    /**
     * 获取用户的聊天会话列表
     * @param userId 用户id
     * @return       会话列表
     */
    List<ConversationDTO> getUserConversations(Long userId);

    /**
     * 获取用户的联系人列表
     * @param userId 用户id
     * @return       联系人列表对象
     */
    ContactsDTO getUserContacts(Long userId);

    /**
     * 修改会话置顶状态
     * @param isGroup        是否群聊
     * @param userId         用户id
     * @param contactId      联系人id
     * @param conversationId 会话id
     * @param status         是否置顶
     * @return               是否修改成功
     */
    boolean updateChatTopState(Boolean isGroup, Long userId, Long contactId, Long conversationId, Boolean status);

    /**
     * 修改会话通知状态
     * @param isGroup        是否群聊
     * @param userId         用户id
     * @param contactId      联系人id
     * @param conversationId 会话id
     * @param status         是否消息通知
     * @return               是否修改成功
     */
    boolean updateChatNoticeState(Boolean isGroup, Long userId, Long contactId, Long conversationId, Boolean status);
}
