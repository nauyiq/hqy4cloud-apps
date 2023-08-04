package com.hqy.cloud.apps.blog.service.request;

import com.hqy.cloud.apps.blog.entity.ChatgptConversation;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptConversationVO;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptMessageHistoryVO;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptRoleVO;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptSystemConfigVO;
import com.hqy.cloud.common.bind.R;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:41
 */
public interface ChatgptRequestService {

    /**
     * 获取chatgpt训练好的角色列表.
     * @return {@link ChatgptRoleVO}
     */
    R<List<ChatgptRoleVO>> getEnableChatgptRoles();

    /**
     * 获取某个用户与chatgpt的会话列表
     * @param id user id
     * @return  {@link ChatgptConversationVO}
     */
    R<List<ChatgptConversationVO>> getChatgptConversations(Long id);

    /**
     * 修改chatgpt聊天会话
     * @param conversation {@link ChatgptConversation}
     * @return             result.
     */
    R<Boolean> updateChatgptConversation(ChatgptConversation conversation);

    /**
     * 删除chatgpt聊天会话
     * @param chatId 聊天会话id
     * @param id     用户id
     * @return       result.
     */
    R<Boolean> deleteChatgptConversation(String chatId, Long id);

    /**
     * 清除当前用户与chatgpt的会话记录
     * @param userId 用户id
     * @return       result.
     */
    R<Boolean> clearChatgptConversations(Long userId);

    /**
     * 查询某个用户与chatgpt某个会话的聊天记录
     * @param chatId 会话id
     * @param id     用户id
     * @return      {@link ChatgptMessageHistoryVO}
     */
    R<List<ChatgptMessageHistoryVO>> getChatgptMessages(String chatId, Long id);

    /**
     * 获取chatgpt系统配置
     * @return  {@link ChatgptSystemConfigVO}
     */
    R<ChatgptSystemConfigVO> getChatgptSystemConfig();



}
