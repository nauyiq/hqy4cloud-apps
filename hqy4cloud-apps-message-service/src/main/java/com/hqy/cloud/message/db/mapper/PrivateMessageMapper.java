package com.hqy.cloud.message.db.mapper;

import com.hqy.cloud.db.mybatisplus.BasePlusMapper;
import com.hqy.cloud.message.bind.dto.ChatMessageDTO;
import com.hqy.cloud.message.db.entity.PrivateMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 私聊聊天消息表 Mapper 接口
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
public interface PrivateMessageMapper extends BasePlusMapper<PrivateMessage> {

    /**
     * 查找两个人的聊天记录
     * @param userId          用户id
     * @param contactId       联系人id
     * @param lastRemoveTime 上次移除会话时间
     * @return               聊天记录
     */
    List<ChatMessageDTO> selectMessages(@Param("userId") Long userId, @Param("contactId") Long contactId, @Param("lastRemoveTime") Date lastRemoveTime);

    /**
     * 查找未读消息id列表
     * @param userId    用户id
     * @param contactId 联系人id
     * @return          未读消息id列表
     */
    List<Long> selectUnreadMessageIds(@Param("userId") Long userId, @Param("contactId") Long contactId);

    /**
     * 设置未读消息为已读
     * @param  ids 未读消息id列表
     * @return     是否设置成功
     */
    int readMessages(@Param("ids") List<Long> ids);
}
