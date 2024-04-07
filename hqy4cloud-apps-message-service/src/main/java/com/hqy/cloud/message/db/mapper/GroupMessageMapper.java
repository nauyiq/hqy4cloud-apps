package com.hqy.cloud.message.db.mapper;

import com.hqy.cloud.db.mybatisplus.BasePlusMapper;
import com.hqy.cloud.message.bind.dto.ChatMessageDTO;
import com.hqy.cloud.message.db.entity.GroupMessage;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 群聊消息表 Mapper 接口
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
public interface GroupMessageMapper extends BasePlusMapper<GroupMessage> {

    /**
     * 根据群聊id查找聊天记录
     * @param groupId        群聊id
     * @param lastRemoveTime 上次移除会话时间
     * @return               聊天记录
     */
    List<ChatMessageDTO> selectMessagesByGroupId(@Param("groupId") Long groupId, @Param("lastRemoveTime") Date lastRemoveTime);

    /**
     * 查找被移除群聊用户所能看见的聊天记录
     * @param groupId        群聊id
     * @param lastRemoveTime 最后一次移除会话时间
     * @param exitGroupTime  退出群聊时间
     * @return               聊天记录
     */
    List<ChatMessageDTO> selectRemovedGroupMemberMessages(@Param("groupId") Long groupId, @Param("lastRemoveTime") Date lastRemoveTime, @Param("exitGroupTime") Date exitGroupTime);

    /**
     * 查找未读消息id列表
     * @param userId         用户id
     * @param lastReadDate   上次已读消息的时间
     * @param groupId       群聊id
     * @param maxUnread     查找的最大消息已读数
     * @return              未读消息id
     */
    List<Long> selectUnreadMessageIds(@Param("userId") Long userId, @Param("lastReadDate") Date lastReadDate, @Param("groupId") Long groupId, @Param("maxUnread") Integer maxUnread);


    /**
     * 更新消息内容和类型
     * @param id      消息id
     * @param type    消息类型
     * @param content 消息内容
     * @return        是否成功
     */
    int updateMessageContentAndType(@Param("id") Long id, @Param("type") Integer type, @Param("content") String content);



}
