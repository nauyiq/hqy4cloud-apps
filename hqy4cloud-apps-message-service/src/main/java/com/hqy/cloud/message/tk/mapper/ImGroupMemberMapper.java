package com.hqy.cloud.message.tk.mapper;

import com.hqy.cloud.db.tk.BaseTkMapper;
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
public interface ImGroupMemberMapper extends BaseTkMapper<ImGroupMember, Long> {

    /**
     * 批量新增或更新
     * @param members 群成员表entity {@link ImGroupMember}
     * @return        result.
     */
    int insertOrUpdate(@Param("members") List<ImGroupMember> members);

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

    /**
     * 更新群聊成员信息
     * @param member 群聊成员entity
     * @return       是否更新成功
     */
    int updateMember(@Param("member")ImGroupMember member);

    /**
     * 简单的查询 - 根据群聊id查询所有群聊用户信息
     * 只返回id,群聊id,用户id,角色,和展示名称
     * @param groupId 群聊id
     * @return        群聊成员
     */
    List<ImGroupMember> simpleQueryAllGroupMembers(@Param("groupId") Long groupId);

    /**
     * 移除群聊成员, 伪删除
     * @param groupId 群id
     * @param userId  用户id
     * @return        result
     */
    int removeGroupMember(@Param("groupId")Long groupId, @Param("userId")Long userId);


}
