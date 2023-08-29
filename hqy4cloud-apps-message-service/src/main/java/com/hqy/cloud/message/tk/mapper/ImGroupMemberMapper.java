package com.hqy.cloud.message.tk.mapper;

import com.hqy.cloud.db.tk.PrimaryLessTkMapper;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.tk.entity.ImGroupMember;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:33
 */
@Repository
public interface ImGroupMemberMapper extends PrimaryLessTkMapper<ImGroupMember> {

    /**
     * 查询群聊用户信息
     * @param id       用户id
     * @param groupIds 群聊id集合
     * @return         {@link GroupMemberDTO}
     */
    List<GroupMemberDTO> queryMembers(@Param("id") Long id, @Param("groupIds") List<Long> groupIds);

    /**
     * query group members
     * @param groupId group id
     * @param userIds user ids.
     * @return        group members
     */
    List<ImGroupMember> queryGroupMembers(@Param("groupId") Long groupId,@Param("userIds") List<Long> userIds);
}
