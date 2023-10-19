package com.hqy.cloud.message.tk.service;

import com.hqy.cloud.db.tk.BaseTkService;
import com.hqy.cloud.message.bind.dto.ChatDTO;
import com.hqy.cloud.message.bind.dto.ForwardMessageDTO;
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

    /**
     * 批量删除
     * @param imConversations entities
     */
    void removeConversations(List<ImConversation> imConversations);

    /**
     * 设置会话状态未无效的状态
     * @param userId     用户id
     * @param contactId  联系人id
     * @param isGroup    是否群聊
     * @param removeTime 移除时间
     * @return           result
     */
    boolean deleteConversation(Long userId, Long contactId, boolean isGroup, Long removeTime);

    /**
     * 撤回会话
     * @param conversations 会话列表
     */
    void undoConversations(List<ImConversation> conversations);

    /**
     * 根据转发列表查询最近联系人
     * @param id                    用户id
     * @param conversationForwards  转发列表
     * @return                      最近联系人
     */
    List<ImConversation> queryConversationsByForwards(Long id, List<ForwardMessageDTO.Forward> conversationForwards);
}
