package com.hqy.cloud.message.service;

import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
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
     * 发消息
     * @param id      from user id
     * @param message {@link ImMessageDTO}
     * @return        {@link ImMessageVO}
     */
    ImMessageVO sendImMessage(Long id, ImMessageDTO message);

    /**
     * insert message
     * @param id       from user id
     * @param message         {@link ImMessageDTO}
     * @param imConversation  {@link ImConversation}
     * @return                {@link ImMessageVO}
     */
//    ImMessageVO insertImMessage(Long id, ImMessageDTO message, ImConversation imConversation);

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
