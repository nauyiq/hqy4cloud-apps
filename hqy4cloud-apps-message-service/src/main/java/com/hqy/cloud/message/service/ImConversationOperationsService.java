package com.hqy.cloud.message.service;

import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.tk.entity.ImConversation;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/31 16:59
 */
public interface ImConversationOperationsService {

    /**
     * query user conversation list.
     * @param id user id
     * @return   conversations.
     */
    List<ConversationVO> getImConversations(Long id);

    /**
     * update group chat top status.
     * @param id        user id.
     * @param groupId   group id.
     * @param status    top status.
     * @return          result.
     */
    boolean updateGroupChatTopStatus(Long id, Long groupId, Boolean status);

    /**
     * update private chat top status.
     * @param id        user id.
     * @param contactId contact id.
     * @param status    top status.
     * @return          result.
     */
    boolean updatePrivateChatTopStatus(Long id, Long contactId, Boolean status);

    /**
     * update group chat notice status.
     * @param id        user id.
     * @param groupId   group id.
     * @param status    notice status.
     * @return          result.
     */
    boolean updateGroupChatNoticeStatus(Long id, Long groupId, Boolean status);

    /**
     * update private chat notice status.
     * @param id        user id.
     * @param contactId contact id.
     * @param status    notice status.
     * @return          result.
     */
    boolean updatePrivateChatNoticeStatus(Long id, Long contactId, Boolean status);

    /**
     * send append private chat event.
     * @param imConversation {@link ImConversation}
     * @param unread         unread message number
     * @return               result.
     */
    boolean sendAppendPrivateChatEvent(ImConversation imConversation, Integer unread);

    /**
     * 新增会话
     * @param id     登录用户id
     * @param userId 联系人id
     * @return       {@link ConversationVO}
     */
    ConversationVO addConversation(Long id, Long userId);
}
