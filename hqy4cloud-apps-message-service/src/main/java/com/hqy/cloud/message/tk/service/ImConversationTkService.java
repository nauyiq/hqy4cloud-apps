package com.hqy.cloud.message.tk.service;

import com.hqy.cloud.db.tk.BaseTkService;
import com.hqy.cloud.message.bind.dto.ChatDTO;
import com.hqy.cloud.message.tk.entity.ImConversation;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 13:23
 */
public interface ImConversationTkService extends BaseTkService<ImConversation, Long> {

    /**
     * 批量新增或修改
     * @param imConversations entities
     * @return                result
     */
    boolean insertOrUpdate(List<ImConversation> imConversations);

    /**
     * query group conversation.
     * @param groupId group id.
     * @return        group member conversation
     */
    List<ImConversation> queryGroupConversationMembers(Long groupId);

    /**
     * 查询双方聊天会话
     * @param id        用户id
     * @param contactId 联系人id
     * @param isGroup   是否是群
     * @return          {@link ImConversation} 聊天会话列表
     */
    List<ImConversation> queryConversations(Long id, Long contactId, Boolean isGroup);

    /**
     * 根据用户id查询聊天列表信息.
     * @param userId 用户id
     * @return      {@link ChatDTO}
     */
    List<ChatDTO> queryImChatDTO(Long userId);
}
