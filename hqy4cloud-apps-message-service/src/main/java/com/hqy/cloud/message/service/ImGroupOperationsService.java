package com.hqy.cloud.message.service;

import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:37
 */
public interface ImGroupOperationsService {

    /**
     * 新建群聊
     * @param id          创建者id
     * @param createGroup {@link GroupDTO}
     * @return            result.
     */
    boolean createGroup(Long id, GroupDTO createGroup);

    /**
     * 修改群聊信息
     * @param groupMemberInfo {@link GroupMemberDTO}
     * @param editGroup       {@link GroupDTO}
     * @return                result.
     */
    boolean editGroup(GroupMemberDTO groupMemberInfo, GroupDTO editGroup);

    /**
     * 添加群聊成员
     * @param groupMember {@link GroupMemberDTO}
     * @return             result.
     */
    boolean addGroupMember(GroupMemberDTO groupMember);

    /**
     * 移除群聊成员
     * @param id      用户id
     * @param groupId 群聊id
     * @return        result.
     */
    boolean removeGroupMember(Long id, Long groupId);

    /**
     * 是否是群聊成员
     * @param id      user id
     * @param groupId group id
     * @return        result
     */
    boolean isGroupMember(Long id, Long groupId);
}
