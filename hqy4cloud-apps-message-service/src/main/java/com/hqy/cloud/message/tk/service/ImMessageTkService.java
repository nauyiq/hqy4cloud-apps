package com.hqy.cloud.message.tk.service;

import com.hqy.cloud.db.tk.BaseTkService;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.tk.entity.ImMessage;

import java.util.Date;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:37
 */
public interface ImMessageTkService extends BaseTkService<ImMessage, Long> {

    /**
     * 获取消息未读
     * @param id                接收人
     * @param messageUnreadList 发送人 request params
     * @return                  {@link MessageUnreadDTO}
     */
    List<MessageUnreadDTO> queryUnread(Long id, List<MessageUnreadDTO> messageUnreadList);

    /**
     * update message is read
     * @param unreadMessageIds unread message ids.
     * @return                 result.
     */
    boolean updateMessagesRead(List<Long> unreadMessageIds);

    /**
     * 查询指定时间之前的聊天记录
     * @param dateTime 时间
     * @return         聊天记录
     */
    List<ImMessage> queryMessagesByBeforeTimes(Date dateTime);
}
