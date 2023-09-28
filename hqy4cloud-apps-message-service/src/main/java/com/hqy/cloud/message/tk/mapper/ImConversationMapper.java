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
}
