package com.hqy.cloud.message.tk.mapper;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.message.bind.dto.ChatDTO;
import com.hqy.cloud.message.tk.entity.ImConversation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 13:22
 */
@Repository
public interface ImConversationMapper extends BaseTkMapper<ImConversation, Long> {

    /**
     * 批量新增或修改
     * @param imConversations entities
     * @return                result
     */
    int insertOrUpdate(@Param("conversations") List<ImConversation> imConversations);

    /**
     * search group conversation members.
     * just return conversation id, userId, contactId
     * @param groupId group id
     * @return        group conversation members.
     */
    List<ImConversation> queryGroupConversationMembers(@Param("groupId") Long groupId);

    /**
     * 查找私人聊天会话
     * @param id        用户id
     * @param contactId 联系人id
     * @return          会话列表
     */
    List<ImConversation> queryPrivateConversations(@Param("id") Long id, @Param("contactId") Long contactId);

    /**
     * 根据用户id查询聊天列表信息.
     * @param userId 用户id
     * @return      {@link ChatDTO}
     */
    List<ChatDTO> queryImChatDTO(@Param("userId") Long userId);

    /**
     * 不根据主键删除， 根据唯一索引删除.
     * @param conversations 会话列表
     */
    void removeConversations(@Param("conversations") List<ImConversation> conversations);

    /**
     * 设置会话状态未无效的状态
     * @param userId     用户id
     * @param contactId  联系人id
     * @param isGroup    是否群聊
     * @param deleted    删除时间
     * @return           result
     */
    int deleteConversation(@Param("userId") Long userId, @Param("contactId") Long contactId, @Param("isGroup") int isGroup, @Param("deleted") Long deleted);
}
