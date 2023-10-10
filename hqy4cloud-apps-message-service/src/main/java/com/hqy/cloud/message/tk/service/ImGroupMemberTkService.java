package com.hqy.cloud.message.tk.service;

import com.hqy.cloud.db.tk.BaseTkService;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.tk.entity.ImGroupMember;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:31
 */
public interface ImGroupMemberTkService extends BaseTkService<ImGroupMember,Long> {

    /**
     * 批量新增或更新
     * @param members 群成员表entity {@link ImGroupMember}
     * @return        result.
     */
    boolean insertOrUpdate(List<ImGroupMember> members);

    /**
     * 查询某个用户加入的群聊信息
     * @param id       用户id
     * @param groupIds 群聊id集合
     * @return         {@link GroupMemberDTO}
     */
    List<GroupMemberDTO> queryMembers(Long id, List<Long> groupIds);

    /**
     * 根据群聊id和成员id集合查询群聊成员信息
     * @param groupId group id
     * @param userIds user ids.
     * @return        group members
     */
    List<ImGroupMember> queryGroupMembers(Long groupId, List<Long> userIds);

    /**
     * 更新群聊成员信息
     * @param member 群聊成员entity
     * @return       是否更新成功
     */
    Boolean updateMember(ImGroupMember member);


    /**
     * 简单的查询 - 根据群聊id查询所有群聊用户信息
     * 只返回群聊id,用户id,角色,和展示名称,创建时间
     * @param groupId 群聊id
     * @return        群聊成员
     */
    List<ImGroupMember> simpleQueryAllGroupMembers(Long groupId);

    /**
     * 移除群聊成员, 伪删除
     * @param groupId 群id
     * @param userId  用户id
     * @return        result
     */
    boolean removeGroupMember(Long groupId, Long userId);


}
