package com.hqy.cloud.message.db.service;

import com.hqy.cloud.db.mybatisplus.BasePlusService;
import com.hqy.cloud.message.bind.dto.ChatMessageDTO;
import com.hqy.cloud.message.db.entity.GroupConversation;
import com.hqy.cloud.message.db.entity.GroupMessage;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 群聊消息表 服务类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
public interface IGroupMessageService extends BasePlusService<GroupMessage> {

    /**
     * 根据群聊id查找聊天记录
     * @param groupId        群聊id
     * @param lastRemoveTime 上次移除会话时间
     * @return               聊天记录
     */
    List<ChatMessageDTO> selectMessagesByGroupId(Long groupId, Long lastRemoveTime);

    /**
     * 查找被移除群聊用户所能看见的聊天记录
     * @param groupConversation 群聊会话
     * @return                  聊天记录
     */
    List<ChatMessageDTO> selectRemovedGroupMemberMessages(GroupConversation groupConversation);

    /**
     * 查找群聊未读消息列表
     * @param userId         用户id
     * @param lastReadTime  上次已读消息时间戳
     * @param groupId       群聊消息id
     * @return              未读消息列表
     */
    List<Long> selectUnreadMessageIds(Long userId, Long lastReadTime, Long groupId);

    /**
     * 撤回消息
     * @param messageId 消息id
     * @param content   撤回内容
     * @return          是否撤回成功
     */
    boolean undoMessage(Long messageId, String content);



}
