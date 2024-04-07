package com.hqy.cloud.message.db.mapper;

import com.hqy.cloud.db.mybatisplus.BasePlusMapper;
import com.hqy.cloud.message.bind.dto.*;
import com.hqy.cloud.message.db.entity.GroupMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * im群聊成员表 Mapper 接口
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-05
 */
public interface GroupMemberMapper extends BasePlusMapper<GroupMember> {

    /**
     * 根据群聊id和用户id查找id
     * @param groupId 群聊id
     * @param userId  用户id
     * @return        主键
     */
    Long selectIdByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 基于唯一索引的插入或更新
     * @param groupMembers 实体对象
     * @return             是否成功
     */
    int insertOrUpdate(@Param("members") List<GroupMember> groupMembers);

    /**
     * 伪删除群聊成员
     * @param groupId 群聊id
     * @param userId  用户id
     * @return        是否成功
     */
    int removeMember(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 真实删除群聊成员
     * @param groupId 群聊id
     * @param userId  用户id
     * @return        是否成功
     */
    int realRemoveMember(@Param("groupId") Long groupId, @Param("userId") Long userId);

    /**
     * 获取群聊以及群聊成员信息, 不根据deleted where限制
     * @param userId  群聊成员id
     * @param groupId 群聊id
     * @return        群聊成员信息
     */
    GroupMemberDTO getGroupMemberInfoNotUsingDeleted(@Param("userId") Long userId, @Param("groupId") Long groupId);

    /**
     * 查询群聊成员id集合
     * @param groupId 群聊id
     * @return        群聊成员id集合
     */
    Set<Long> getGroupMemberIds(@Param("groupId") Long groupId);

    /**
     * 批量获取群聊成员id集合
     * @param groupIds 群聊id集合
     * @return         群聊成员id集合
     */
    List<GroupMemberIdsDTO> getGroupMembers(@Param("groupIds") Set<Long> groupIds);

    /**
     * 查找群聊用户信息
     * @param groupId        群聊id
     * @param groupMemberIds 群聊成员id集合
     * @return               群聊成员用户信息
     */
    Set<ImUserInfoDTO> getGroupMemberUserInfo(@Param("groupId") Long groupId, @Param("groupMemberIds") Set<Long> groupMemberIds);

    /**
     * 获取群聊联系人列表
     * @param userId 用户id
     * @return       群聊联系人列表
     */
    List<ContactDTO> queryContactsByUserId(@Param("userId") Long userId);

    /**
     * 获取群聊成员
     * @param groupId 群聊id
     * @return        群聊成员
     */
    List<GroupMemberInfoDTO> queryGroupMembers(@Param("groupId")Long groupId);

    /**
     * 修改置顶状态
     * @param userId    用户id
     * @param groupId   群聊id
     * @param status    置顶状态
     * @return          是否修改成功
     */
    int updateTopState(@Param("userId")Long userId, @Param("groupId") Long groupId, @Param("status") Boolean status);

    /**
     * 修改通知状态
     * @param userId    用户id
     * @param groupId   群聊id
     * @param status    通知状态
     * @return          是否修改成功
     */
    int updateNoticeState(@Param("userId")Long userId, @Param("groupId") Long groupId, @Param("status") Boolean status);
}
