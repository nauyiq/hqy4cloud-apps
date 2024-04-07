package com.hqy.cloud.message.db.mapper;

import com.hqy.cloud.db.mybatisplus.BasePlusMapper;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.db.entity.PrivateConversation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 私聊聊天会话表 Mapper 接口
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-06
 */
public interface PrivateConversationMapper extends BasePlusMapper<PrivateConversation> {

    /**
     * 查询会话消息并且统计消息未读数
     * @param userId 用户id
     * @return       聊会会话DTO
     */
    List<ConversationDTO> queryConversationInfoAndMessageUnreadCountByUserId(@Param("userId") Long userId);

    /**
     * 查询会话消息, 不统计未读数
     * @param userId 用户id
     * @return       聊会会话DTO
     */
    List<ConversationDTO> queryConversationsByUserId(@Param("userId") Long userId);

    /**
     * 查询好友会话信息
     * @param userId   用户id
     * @param friendId 好友id
     * @return         会话信息
     */
    ConversationDTO queryConversationInfoByUserIdAndFriendId(@Param("userId")Long userId, @Param("friendId") Long friendId);


    /**
     * 查询会话id根据唯一主键
     * @param userId    用户id
     * @param contactId 联系人id
     * @return          会话id
     */
    Long queryIdByUniqueIndex(@Param("userId")Long userId, @Param("contactId") Long contactId);

    /**
     * 批量新增或更新
     * @param conversations 会话列表
     * @return              行数
     */
    int insertOrUpdate(@Param("conversations") List<PrivateConversation> conversations);

    /**
     * 根据duplicate key更新类型和内容
     * @param conversations 会话列表
     * @return              行数
     */
    int duplicateUpdateTypeAndContent(@Param("conversations") List<PrivateConversation> conversations);

    /**
     * 根据用户id和好友id查询双方会话
     * @param userId   用户id
     * @param friendId 好友id
     * @return         会话列表
     */
    List<PrivateConversation> queryConversationsByUserIdAndFriendId(@Param("userId")Long userId, @Param("friendId")Long friendId);

    /**
     * 伪删除会话
     * @param userId   用户id
     * @param friendId 好友id
     * @return         行数
     */
    int removeConversation(@Param("userId") Long userId, @Param("contactId") Long friendId);

    /**
     * 更新会话展示名
     * @param userId      用户id
     * @param contactId   联系人id
     * @param displayName 展示名
     * @return            是否更新成功
     */
    int updateConversationDisplayName(@Param("userId") Long userId, @Param("contactId") Long contactId, @Param("displayName") String displayName);

    /**
     * 更新会话，撤回消息
     * @param userId    用户id
     * @param contactId 联系人id
     * @return          是否撤回成功
     */
    int updateConversationUndoMessage(@Param("userId") Long userId, @Param("contactId") Long contactId);

    /**
     * 修改会话的置顶状态
     * @param conversationId 会话id
     * @param status         置顶状态
     * @return               是否修改成功
     */
    int updateConversationTopState(@Param("id") Long conversationId, @Param("status") Boolean status);

    /**
     * 修改会话的通知状态
     * @param conversationId 会话id
     * @param status         通知状态
     * @return               是否修改成功
     */
    int updateConversationNoticeState(@Param("id") Long conversationId, @Param("status") Boolean status);


}
