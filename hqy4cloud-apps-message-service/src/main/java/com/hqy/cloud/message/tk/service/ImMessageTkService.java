package com.hqy.cloud.message.tk.service;

import com.hqy.cloud.db.tk.BaseTkService;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.tk.entity.ImMessage;

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
}
