package com.hqy.cloud.message.db.service;

import com.hqy.cloud.db.mybatisplus.BasePlusService;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.db.entity.GroupConversation;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 群聊聊天会话表 服务类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-06
 */
public interface IGroupConversationService extends BasePlusService<GroupConversation> {

    /**
     * 更新群聊会话
     * @param groupMembers 群聊成员id集合
     * @param groupId      群聊id
     * @param messageType  消息类型
     * @param message      消息体对象
     * @return             是否更新成功
     */
    boolean updateGroupConversations(Set<Long> groupMembers, Long groupId, Integer messageType, ImMessageDTO message);


    /**
     * 根据用户id查询群聊会话
     * @param userId 用户id
     * @return       群聊会话列表
     */
    List<ConversationDTO> queryConversationsByUserId(Long userId);

    /**
     * 根据用户id和群聊id查询会话信息
     * @param id        用户id
     * @param groupId   群聊id
     * @return          会话信息
     */
    ConversationDTO queryConversationInfoByUserIdAndGroupId(Long id, Long groupId);

    /**
     * 查询群聊会话
     * @param userId  用户id
     * @param groupId 群聊id
     * @return        群聊会话
     */
    GroupConversation queryByUserIdAndGroupId(Long userId, Long groupId);

    /**
     * 更新会话消息为消息撤回
     * @param userId    用户id
     * @param groupId   群聊id
     * @param content   内容
     * @return          是否更新成功
     */
    boolean updateConversationUndoMessage(Long userId, Long groupId, String content);

    /**
     * 新增追加群聊会话事件
     * @param groupConversations 群聊会话列表
     * @param groupAvatar        群聊头像
     * @param groupName          群聊名
     * @param groupCreator       群聊创建者id
     */
    void sendAppendGroupChatEvent(List<GroupConversation> groupConversations, String groupAvatar, String groupName, Long groupCreator);

    /**
     * 批量新增或更新会话
     * @param conversations 会话列表
     * @return              是否成功
     */
    boolean insertOrUpdate(List<GroupConversation> conversations);

    /**
     * 新增或更新会话，并且返回带有id的会话
     * @param conversations 会话列表
     * @return              会话列表
     */
    List<GroupConversation> insertOrUpdateReturnConversations(List<GroupConversation> conversations);

    /**
     * 修改群聊会话角色
     * @param groupId    群聊id
     * @param userId    用户id
     * @param role      角色
     * @param updated   更新时间
     * @return          是否成功
     */
    boolean updateGroupConversationRoleAndUpdated(Long groupId, Long userId, Integer role, Date updated);

    /**
     * 移除群聊会话，伪删除
     * @param groupId 群聊id
     * @param userId  用户id
     * @return        是否成功
     */
    boolean removeGroupConversation(Long groupId, Long userId);

    /**
     * 移除群聊会话，真删除
     * @param groupId 群聊id
     * @param userId  用户id
     * @return        是否成功
     */
    boolean realRemoveGroupConversation(Long groupId, Long userId);

    /**
     * 修改会话置顶状态
     * @param conversationId 会话id
     * @param status         状态
     * @return               是否修改成功
     */
    boolean updateConversationTopState(Long conversationId, Boolean status);

    /**
     * 修改会话通知状态
     * @param conversationId 会话id
     * @param status         状态
     * @return               是否修改成功
     */
    boolean updateConversationNoticeState(Long conversationId, Boolean status);


}
