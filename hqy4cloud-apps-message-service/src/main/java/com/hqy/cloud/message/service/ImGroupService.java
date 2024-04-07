package com.hqy.cloud.message.service;

import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.db.entity.GroupMember;
import com.hqy.cloud.message.db.entity.UserSetting;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 群聊相关操作service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/5
 */
public interface ImGroupService {

    /**
     * 获取某个群的所有群聊成员
     * @param groupId 群聊id
     * @return        群聊成员id集合
     */
    Set<Long> getGroupMembers(Long groupId);

    /**
     * 获取群聊用户的信息
     * @param groupId        群聊id
     * @param groupMemberIds 群聊成员id集合
     * @return               返回map, key = 用户id； value = 用户信息
     */
    Map<Long, UserInfoVO> getGroupMemberUserInfo(Long groupId, Set<Long> groupMemberIds);

    /**
     * 批量获取群聊用户id
     * @param groupIds 群聊id列表
     * @return         key: 群聊id， value： groupId列表
     */
    Map<Long, List<Long>> getGroupMemberIdMaps(Set<Long> groupIds);

    /**
     * 创建群聊
     * @param creator      创建者id
     * @param createGroup  群聊信息
     * @param groupMembers 群聊成员信息
     * @return            是否创建成功
     */
    boolean createGroup(Long creator, GroupDTO createGroup, List<UserSetting> groupMembers);

    /**
     * 修改群聊信息
     * @param id              用户id
     * @param username        用户名
     * @param groupMemberInfo 群聊成员信息
     * @param editGroup       修改的群聊数据
     * @return                是否修改成功
     */
    boolean editGroup(Long id, String username, GroupMemberDTO groupMemberInfo, GroupDTO editGroup);

    /**
     * 判断用户是否是群聊成员
     * @param groupId 群聊id
     * @param userId  用户id
     * @return        是否是群聊成员
     */
    boolean isGroupMember(Long groupId, Long userId);

    /**
     * 添加群聊成员
     * @param info         邀请人信息、群信息
     * @param userIds      用户列表
     * @return             是否添加成功
     */
    boolean addGroupMembers(GroupMemberDTO info, List<Long> userIds);

    /**
     * 移除群聊成员
     * @param member       被移除的群聊成员
     * @return             是否移除成功
     */
    boolean removeGroupMember(GroupMemberDTO member);

    /**
     * 退出群聊
     * @param groupMemberInfo 群成员信息
     * @return                是否退出成功
     */
    boolean exitGroup(GroupMemberDTO groupMemberInfo);

    /**
     * 删除群聊
     * @param groupMemberInfo 创建者群信息
     * @return                是否删除成功
     */
    boolean deleteGroup(GroupMemberDTO groupMemberInfo);



}
