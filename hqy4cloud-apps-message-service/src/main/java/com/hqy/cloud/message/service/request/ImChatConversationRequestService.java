package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.ImChatConfigDTO;
import com.hqy.cloud.message.bind.vo.ContactsVO;
import com.hqy.cloud.message.bind.vo.ConversationVO;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/6
 */
public interface ImChatConversationRequestService {

    /**
     * 获取当前用户的聊天会话列表
     * @param userId 用户id
     * @return       聊天会话列表.
     */
    R<List<ConversationVO>> getImUserConversations(Long userId);

    /**
     * 获取当前用户的联系人列表
     * @param userId 用户id
     * @return       联系人列表
     */
    R<ContactsVO> getImUserContacts(Long userId);

    /**
     * 修改联系人聊天置顶设置
     * @param id          user id.
     * @param chatConfig {@link ImChatConfigDTO}
     * @return           R.
     */
    R<Boolean> updateChatTopState(Long id, ImChatConfigDTO chatConfig);

    /**
     * 修改联系人聊天消息提醒设置
     * @param id          user id.
     * @param chatConfig {@link ImChatConfigDTO}
     * @return           R.
     */
    R<Boolean> updateChatNoticeState(Long id, ImChatConfigDTO chatConfig);

    /**
     * 新增会话
     * @param id        登录用户id
     * @param contactId 联系人id
     * @param isGroup   是否是群聊
     * @return          新增好的会话
     */
    R<ConversationVO> addConversation(Long id, Long contactId, Boolean isGroup);

    /**
     * 删除会话
     * @param userId         用户id
     * @param group          是否是群聊
     * @param conversationId 会话id
     * @return               R.
     */
    R<Boolean> deleteConversation(Long userId, boolean group, Long conversationId);



}
