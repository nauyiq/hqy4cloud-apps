package com.hqy.cloud.message.db.mapper;

import com.hqy.cloud.db.mybatisplus.BasePlusMapper;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.bind.dto.UnreadDTO;
import com.hqy.cloud.message.db.entity.GroupConversation;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 群聊聊天会话表 Mapper 接口
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-06
 */
public interface GroupConversationMapper extends BasePlusMapper<GroupConversation> {

    /**
     * 查询群聊会话
     * @param groupId 群id
     * @param userIds 用户id集合
     * @return        群聊会话集合
     */
    List<GroupConversation> queryConversationsByGroupIdAndUserIds(@Param("groupId") Long groupId, @Param("userIds") List<Long> userIds);

    /**
     * 更新群聊会话列表
     * @param groupMembers 群聊成员
     * @param groupId      群聊id
     * @param content      内容
     * @param messageType  消息类型
     * @param now          当前时间戳
     * @return             行数
     */
    int updateGroupConversations(@Param("groupMembers") Set<Long> groupMembers, @Param("groupId")  Long groupId,
                                 @Param("content") String content, @Param("messageType") Integer messageType, @Param("now") Long now);

    /**
     * 根据用户id查询群聊会话
     * @param userId 用户id
     * @return       群聊会话列表
     */
    List<ConversationDTO> queryConversationsByUserId(@Param("userId") Long userId);

    /**
     * 查询群聊会话
     * @param id      用户id
     * @param groupId 群聊id
     * @return        群聊会话
     */
    ConversationDTO queryConversationInfoByUserIdAndGroupId(@Param("userId") Long id, @Param("groupId") Long groupId);

    /**
     * 查询群聊会话的未读消息数
     * @param userId        用户id
     * @param conversations 需要查询未读数的群聊会话
     * @return              未读消息数
     */
    List<UnreadDTO> queryGroupUnreadByConversations(@Param("userId") Long userId, @Param("conversations") List<ConversationDTO> conversations);

    /**
     * 更新群聊会话, 消息撤回
     * @param userId    用户id
     * @param groupId   群聊id
     * @param content   内容
     * @param type      消息类型
     * @return          行数
     */
    int updateConversationTypeAndContent(@Param("userId") Long userId, @Param("groupId") Long groupId, @Param("content") String content, @Param("type") Integer type);

    /**
     * 新增或更新会话
     * @param conversations 群聊实体列表
     * @return              行数
     */
    int insertOrUpdate(@Param("conversations") List<GroupConversation> conversations);

    /**
     * 修改群聊会话角色
     * @param groupId  群聊id
     * @param userId   用户角色
     * @param role     角色
     * @param updated 更新时间
     * @return        是否成功
     */
    int updateGroupConversationRoleAndUpdated(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("role") Integer role, @Param("updated")  Date updated);

    /**
     * 移除群聊会话，伪删除
     * @param groupId 群聊id
     * @param userId  用户id
     * @return        是否成功
     */
    int removeGroupConversation(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 移除群聊会话，真实删除
     * @param groupId 群聊id
     * @param userId  用户id
     */
    void realRemoveGroupConversation(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 修改会话置顶状态
     * @param conversationId 会话id
     * @param status         状态
     * @return               是否修改成功
     */
    int updateConversationTopState(@Param("id") Long conversationId, @Param("status") Boolean status);

    /**
     * 修改会话通知状态
     * @param conversationId 会话id
     * @param status         状态
     * @return               是否修改成功
     */
    int updateConversationNoticeState(@Param("id") Long conversationId, @Param("status") Boolean status);



}
