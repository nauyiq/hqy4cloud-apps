package com.hqy.cloud.apps.blog.service.opeations.impl;

import com.corundumstudio.socketio.SocketIOClient;
import com.hqy.cloud.apps.blog.config.Constants;
import com.hqy.cloud.apps.blog.entity.ChatgptConversation;
import com.hqy.cloud.apps.blog.entity.ChatgptMessageHistory;
import com.hqy.cloud.apps.blog.entity.ChatgptRole;
import com.hqy.cloud.apps.blog.entity.Config;
import com.hqy.cloud.apps.blog.service.opeations.ChatgptOperationService;
import com.hqy.cloud.apps.blog.service.tk.ChatgptConversationTkService;
import com.hqy.cloud.apps.blog.service.tk.ChatgptMessageHistoryTkService;
import com.hqy.cloud.apps.blog.service.tk.ChatgptRoleTkService;
import com.hqy.cloud.apps.blog.service.tk.ConfigTkService;
import com.hqy.cloud.apps.blog.socket.listener.ChatgptMessageListener;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptSystemConfigVO;
import com.hqy.cloud.chatgpt.common.dto.ChatGptMessageReq;
import com.hqy.cloud.chatgpt.service.OpenAiChatgptService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.JsonUtil;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:38
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatgptOperationServiceImpl implements ChatgptOperationService {
    private final TransactionTemplate template;
    private final ConfigTkService configTkService;
    private final ChatgptRoleTkService chatgptRoleTkService;
    private final ChatgptConversationTkService chatgptConversationTkService;
    private final ChatgptMessageHistoryTkService chatgptMessageHistoryTkService;
    private final OpenAiChatgptService openAiChatgptService;

    @Override
    public List<ChatgptRole> getChatgptRolesByStatus(Boolean status) {
        Example example = new Example(ChatgptRole.class);
        Example.Criteria criteria = example.createCriteria();
        if (status != null) {
            criteria.andEqualTo("status", status);
        }
        example.orderBy("sort").desc();
        return chatgptRoleTkService.queryByExample(example);
    }

    @Override
    public List<ChatgptConversation> getChatgptConversationsByUserId(Long id) {
        Example example = new Example(ChatgptConversation.class);
        Example.Criteria criteria = example.createCriteria();
        if (id != null) {
            criteria.andEqualTo("userId", id);
        }
        example.orderBy("created").desc();
        return chatgptConversationTkService.queryByExample(example);
    }

    @Override
    public boolean updateChatgptConversations(ChatgptConversation conversation) {
        return chatgptConversationTkService.updateSelective(conversation);
    }

    @Override
    public boolean deleteChatgptConversation(String chatId, Long userId) {
        ChatgptConversation conversation = chatgptConversationTkService.queryById(chatId);
        if (conversation != null) {
            Long id = conversation.getUserId();
            if (id.equals(userId)) {
                Boolean execute = template.execute(status -> {
                    try {
                        //删除会话
                        AssertUtil.isTrue(chatgptConversationTkService.deleteByPrimaryKey(chatId), "Failed execute to delete chatgpt conversation.");
                        //删除聊天记录
                        ChatgptMessageHistory history = new ChatgptMessageHistory(chatId, userId);
                        AssertUtil.isTrue(chatgptMessageHistoryTkService.delete(history), "Failed execute to delete chatgpt messages.");
                        return true;
                    } catch (Throwable cause) {
                        status.setRollbackOnly();
                        log.error(cause.getMessage() + " chatId = {}, userId = {}.", chatId, userId);
                        return false;
                    }
                });
                return Boolean.TRUE.equals(execute);
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean clearChatgptConversations(Long userId) {
        ChatgptConversation conversation = new ChatgptConversation(userId);
        List<ChatgptConversation> conversations = chatgptConversationTkService.queryList(conversation);
        if (CollectionUtils.isEmpty(conversations)) {
            return true;
        }
        Boolean execute = template.execute(status -> {
            try {
                //删除用户所有会话
                AssertUtil.isTrue(chatgptConversationTkService.delete(conversation), "Failed execute to delete user all chatgpt conversations.");
                //删除用户所有聊天记录
                ChatgptMessageHistory history = new ChatgptMessageHistory(userId);
                AssertUtil.isTrue(chatgptMessageHistoryTkService.delete(history), "Failed execute to delete user all chatgpt messages.");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage() + " userId = {}.", userId);
                return false;
            }
        });
        return Boolean.TRUE.equals(execute);
    }

    @Override
    public List<ChatgptMessageHistory> getChatgptConversationMessageHistory(String chatId, Long userId) {
        Example example = new Example(ChatgptMessageHistory.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(chatId)) {
            criteria.andEqualTo("chatId", chatId);
        }
        if (userId != null) {
            criteria.andEqualTo("userId", userId);
        }
        example.orderBy("created").desc();
        return chatgptMessageHistoryTkService.queryByExample(example);
    }

    @Override
    public ChatgptSystemConfigVO getChatgptSystemConfig() {
        Config config = configTkService.queryOne(new Config());
        if (config == null) {
            return null;
        }
        String title = StringUtils.isBlank(config.getChatTitle()) ? Constants.DEFAULT_CHATGPT_DEFAULT_TITLE : config.getChatTitle();
        List<String> models = StringUtils.isBlank(config.getChatgptModels()) ? Constants.DEFAULT_CHATGPT_MODELS : JsonUtil.toList(config.getChatgptModels(), String.class);
        return new ChatgptSystemConfigVO(title, models);
    }

    @Override
    public void streamChatCompletion(Long userId, ChatgptMessageListener.ChatgptMessage data, SocketIOClient client) {
        String chatId = data.getChatId();
        List<ChatMessage> history;
        if (StringUtils.isNotBlank(chatId)) {
            // 查找有无历史记录
//            history = chatgptMessageHistoryTkService.findHistory(userId, chatId);
        } else {
            history = Collections.emptyList();
        }

      /*  ChatGptMessageReq req = new ChatGptMessageReq();
        req.setModel(data.getModel());
        req.setPrompt(data.getPrompt());
        req.setHistory(history);*/


//        openAiChatgptService.streamChatCompletion(userId, )


    }
}
