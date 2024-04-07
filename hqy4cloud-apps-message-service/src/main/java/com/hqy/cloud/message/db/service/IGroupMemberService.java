package com.hqy.cloud.message.db.service;

import com.hqy.cloud.db.mybatisplus.BasePlusService;
import com.hqy.cloud.message.bind.dto.*;
import com.hqy.cloud.message.db.entity.GroupMember;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * im群聊成员表 服务类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-05
 */
public interface IGroupMemberService extends BasePlusService<GroupMember> {

    /**
     * 判断是否群聊成员
     * @param groupId 群聊id
     * @param userId  用户id
     * @return        是否是群聊成员
     */
    boolean isGroupMember(Long groupId, Long userId);

    /**
     * 基于唯一索引的插入或更新
     * @param groupMembers 实体对象
     * @return             是否成功
     */
    boolean insertOrUpdate(List<GroupMember> groupMembers);

    /**
     * 伪删除群聊成员
     * @param groupId 群聊id
     * @param userId  用户id
     * @return        是否成功
     */
    boolean removeMember(Long groupId, Long userId);

    /**
     * 真实移除群聊成员
     * @param groupId 群聊id
     * @param userId  用户id
     * @return        是否成功
     */
    boolean realRemoveMember(Long groupId, Long userId);

    /**
     * 获取群聊以及群聊成员信息
     * @param id      群聊成员id
     * @param groupId 群聊id
     * @return        群聊成员信息
     */
    GroupMemberDTO getGroupMemberInfo(Long id, Long groupId);

    /**
     * 查询群聊成员id集合
     * @param groupId 群聊id
     * @return        群聊成员id集合
     */
    Set<Long> getGroupMemberIds(Long groupId);

    /**
     * 查找群聊用户信息
     * @param groupId        群聊id
     * @param groupMemberIds 群聊成员id集合
     * @return               群聊成员用户信息
     */
    Set<ImUserInfoDTO> getGroupMemberUserInfo(Long groupId, Set<Long> groupMemberIds);

    /**
     * 获取群聊联系人列表
     * @param userId 用户id
     * @return       群聊联系人列表
     */
    List<ContactDTO> queryContactsByUserId(Long userId);

    /**
     * 获取群聊成员
     * @param groupId 群聊id
     * @return        群聊成员
     */
    List<GroupMemberInfoDTO> queryGroupMembers(Long groupId);

    /**
     * 批量获取群聊成员id集合
     * @param groupIds 群聊id集合
     * @return         群聊成员id集合
     */
    List<GroupMemberIdsDTO> getGroupMembers(Set<Long> groupIds);

    /**
     * 根据用户id和群聊id查找
     * @param id      用户id
     * @param groupId 群聊id
     * @return        实体
     */
    GroupMember getByUserIdAndGroupId(Long id, Long groupId);

    /**
     * 修改置顶状态
     * @param userId    用户id
     * @param groupId   群聊id
     * @param status    置顶状态
     * @return          是否修改成功
     */
    boolean updateTopState(Long userId, Long groupId, Boolean status);

    /**
     * 修改通知状态
     * @param userId    用户id
     * @param groupId   群聊id
     * @param status    通知
     * @return          是否修改成功
     */
    boolean updateNoticeState(Long userId, Long groupId, Boolean status);
}
