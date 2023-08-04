package com.hqy.cloud.apps.blog.service.opeations;

import com.corundumstudio.socketio.SocketIOClient;
import com.hqy.cloud.apps.blog.entity.ChatgptConversation;
import com.hqy.cloud.apps.blog.entity.ChatgptMessageHistory;
import com.hqy.cloud.apps.blog.entity.ChatgptRole;
import com.hqy.cloud.apps.blog.socket.listener.ChatgptMessageListener;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptSystemConfigVO;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:38
 */
public interface ChatgptOperationService {

    /**
     * 根据状态查询ChatgptRoles
     * @param status 状态
     * @return       {@link ChatgptRole}
     */
    List<ChatgptRole> getChatgptRolesByStatus(Boolean status);

    /**
     * 根据用户id与chatgpt的会话列表
     * @param id 用户id
     * @return   {@link ChatgptConversation}
     */
    List<ChatgptConversation> getChatgptConversationsByUserId(Long id);

    /**
     * 修改chatgpt会话
     * @param conversation 会话
     * @return             result
     */
    boolean updateChatgptConversations(ChatgptConversation conversation);
    /**
     * 删除chatgpt聊天会话
     * @param chatId 聊天会话id
     * @param userId 用户id
     * @return       result.
     */
    boolean deleteChatgptConversation(String chatId, Long userId);

    /**
     * 删除chatgpt 所有聊天记录
     * @param userId 用户id
     * @return       result
     */
    boolean clearChatgptConversations(Long userId);

    /**
     * 获取用户的某个会话与chatgpt的聊天记录
     * @param chatId 会话id
     * @param id     用户id
     * @return       {@link ChatgptMessageHistory}
     */
    List<ChatgptMessageHistory> getChatgptConversationMessageHistory(String chatId, Long id);

    /**
     * 获取chatgpt系统配置
     * @return {@link ChatgptSystemConfigVO}
     */
    ChatgptSystemConfigVO getChatgptSystemConfig();

    /**
     * 流式聊天与chatgpt
     * @param userId 用户id
     * @param data   消息体
     * @param client 当前用户socket.io客户端
     */
    void streamChatCompletion(Long userId, ChatgptMessageListener.ChatgptMessage data, SocketIOClient client);
}
