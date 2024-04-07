package com.hqy.cloud.message.db.service;

import com.hqy.cloud.db.mybatisplus.BasePlusService;
import com.hqy.cloud.message.bind.dto.ChatMessageDTO;
import com.hqy.cloud.message.db.entity.PrivateMessage;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 私聊聊天消息表 服务类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
public interface IPrivateMessageService extends BasePlusService<PrivateMessage> {

    /**
     * 查找两个人的聊天记录
     * @param userId         用户id
     * @param contactId      联系人id
     * @param lastRemoveTime 上次移除会话时间
     * @return               聊天记录
     */
    List<ChatMessageDTO> selectMessages(Long userId, Long contactId, Long lastRemoveTime);

    /**
     * 查找未读消息id列表
     * @param userId    用户id
     * @param contactId 联系人id
     * @return          未读消息id列表
     */
    List<Long> selectUnreadMessageIds(Long userId, Long contactId);

    /**
     * 设置未读消息为已读
     * @param unreadMessageIds 未读消息id列表
     * @return                 是否设置成功
     */
    boolean readMessages(List<Long> unreadMessageIds);

    /**
     * 更新消息类型为消息撤回
     * @param messageId 消息id
     * @param content   撤回消息内容
     * @return          是否撤回成功
     */
    boolean undoMessage(Long messageId, String content);
}
