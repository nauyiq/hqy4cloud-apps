package com.hqy.cloud.message.service;

import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.tk.entity.ImFriend;
import com.hqy.cloud.message.tk.entity.ImGroupMember;

import java.util.List;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:37
 */
public interface ImGroupOperationsService {

    /**
     * 新建群聊
     * @param creator     创建者id
     * @param groupInfo   {@link GroupDTO}
     * @param friends     好友列表
     * @return            result.
     */
    boolean createGroup(Long creator,  GroupDTO groupInfo, List<ImFriend> friends);

    /**
     * 修改群聊信息
     * @param info             用户信息
     * @param groupMemberInfo {@link GroupMemberDTO}
     * @param editGroup       {@link GroupDTO}
     * @return                result.
     */
    boolean editGroup(AuthenticationInfo info, GroupMemberDTO groupMemberInfo, GroupDTO editGroup);

    /**
     * 添加群聊成员
     * @param groupMemberInfo {@link GroupMemberDTO}
     * @param userIds         新增用户id
     * @return                result.
     */
    boolean addGroupMember(GroupMemberDTO groupMemberInfo, List<Long> userIds);

    /**
     * 移除群聊成员
     * @param operator     操作用户id
     * @param userId       用户id
     * @param groupId      群聊id
     * @return             result.
     */
    boolean removeGroupMember(Long operator, Long userId, Long groupId);

    /**
     * 是否是群聊成员
     * @param id      user id
     * @param groupId group id
     * @return        result
     */
    boolean isGroupMember(Long id, Long groupId);

    /**
     * 退出群聊
     * @param member 群聊成员
     * @return       result
     */
    boolean exitGroup(ImGroupMember member);

    /**
     * 删除群聊
     * @param userId  群主id
     * @param groupId 群id
     * @return        result.
     */
    boolean deleteGroup(Long userId, Long groupId);

    /**
     * return group members remark
     * @param groupId group id.
     * @param members group members id
     * @return        group members remark
     */
    Map<Long, String> getGroupMemberRemark(Long groupId, List<Long> members);



}
