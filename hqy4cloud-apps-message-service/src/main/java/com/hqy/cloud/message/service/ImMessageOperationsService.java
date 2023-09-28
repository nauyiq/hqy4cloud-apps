package com.hqy.cloud.message.service;

import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.entity.ImMessage;

import java.util.List;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @date 2023-08-20 21:41
 */
public interface ImMessageOperationsService {

    /**
     * 获取会话未读消息数
     * @param id                用户id
     * @param messageUnreadList request param {@link MessageUnreadDTO}
     * @return                  key:会话id value:消息未读数
     */
    Map<String, Integer> getConversationUnread(Long id, List<MessageUnreadDTO> messageUnreadList);

    /**
     * 获取用户系统消息未读数
     * @param id 用户id
     * @return   未读消息数
     */
    int getSystemMessageUnread(Long id);


    /**
     * 发消息
     * @param id      from user id
     * @param message {@link ImMessageDTO}
     * @return        {@link ImMessageVO}
     */
    ImMessageVO sendImMessage(Long id, ImMessageDTO message);

    /**
     * 添加系统消息
     * @param send           发送人
     * @param receive        接收人
     * @param message        消息
     * @param conversationId 会话id
     */
    void addSystemMessage(Long send, Long receive, String message, Long conversationId);

    /**
     * 添加简单的消息
     * @param send            发送者
     * @param receive         接收者
     * @param isGroup         是否是群聊
     * @param conversationId  会话id
     * @param groupMembers    群聊用户id集合
     * @param messageType     消息类型
     * @param message         消息内容
     * @return                消息
     */
    ImMessage addSimpleMessage(Long send, Long receive, boolean isGroup, Long conversationId,
                          List<Long> groupMembers, ImMessageType messageType, String message);

    /**
     * read messages
     * @param conversation conversation
     * @return             read messages ids.
     */
    List<String> readMessages(ImConversation conversation);

    /**
     * undo im message
     * @param imMessage message entity.
     * @return          result.
     */
    boolean undoMessage(ImMessage imMessage);


}
