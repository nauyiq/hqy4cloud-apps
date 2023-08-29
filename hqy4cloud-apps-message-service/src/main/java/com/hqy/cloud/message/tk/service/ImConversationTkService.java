package com.hqy.cloud.message.tk.service;

import com.hqy.cloud.db.tk.BaseTkService;
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
}
