package com.hqy.cloud.message.service;

import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;

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

}
