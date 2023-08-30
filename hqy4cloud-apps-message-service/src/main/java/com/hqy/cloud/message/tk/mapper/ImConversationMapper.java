package com.hqy.cloud.message.tk.mapper;

import com.hqy.cloud.db.tk.BaseTkMapper;
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


}
