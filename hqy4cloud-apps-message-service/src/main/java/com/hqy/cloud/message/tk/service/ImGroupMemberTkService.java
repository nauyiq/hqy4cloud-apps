package com.hqy.cloud.message.tk.service;

import com.hqy.cloud.db.tk.PrimaryLessTkService;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.tk.entity.ImGroupMember;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:31
 */
public interface ImGroupMemberTkService extends PrimaryLessTkService<ImGroupMember> {

    /**
     * 查询群聊用户信息
     * @param id       用户id
     * @param groupIds 群聊id集合
     * @return         {@link GroupMemberDTO}
     */
    List<GroupMemberDTO> queryMembers(Long id, List<Long> groupIds);

    /**
     * query group members
     * @param groupId group id
     * @param userIds user ids.
     * @return        group members
     */
    List<ImGroupMember> queryGroupMembers(Long groupId, List<Long> userIds);
}
