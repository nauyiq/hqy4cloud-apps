package com.hqy.cloud.message.db.mapper;

import com.hqy.cloud.db.mybatisplus.BasePlusMapper;
import com.hqy.cloud.message.bind.dto.AddGroupMemberDTO;
import com.hqy.cloud.message.bind.dto.ImUserInfoDTO;
import com.hqy.cloud.message.db.entity.UserSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * im用户设置表 Mapper 接口
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-04
 */
public interface UserSettingMapper extends BasePlusMapper<UserSetting> {


    /**
     * 根据名字查询聊天用户集合
     * @param name 名字
     * @return     用户集合
     */
    List<UserSetting> selectImUserByName(@Param("name") String name);

    /**
     * 查找好友信息
     * @param userId   用户id
     * @param friendId 好友id
     * @return         好友信息
     */
    ImUserInfoDTO selectFriendInfo(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * 查找好友信息
     * @param id       当前登录用户
     * @param friendId 好友id集合, 包括自己
     * @return        好友信息
     */
    ImUserInfoDTO selectFriendInfoById(@Param("id") Long id, @Param("friendId") Long friendId);

    /**
     * 获取双方的好友信息
     * @param userId   用户id
     * @param friendId 好友id
     * @return         好友信息map
     */
    List<ImUserInfoDTO> selectFriendInfosByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * 查找用户昵称
     * @param id 用户id
     * @return   昵称
     */
    String selectNicknameById(@Param("id") Long id);

    /**
     * 启用聊天账号
     * @param id 账号id
     * @return   是否成功
     */
    int settingUserStatusEnabled(@Param("id") Long id);

    /**
     * 查找用户名
     * @param ids id列表
     * @return    用户列表
     */
    List<UserSetting> selectUsernames(@Param("ids") List<Long> ids);

    /**
     * 查找添加群聊成员的用户信息
     * @param groupId 群聊id
     * @param userIds 用户id列表
     * @return        添加到群聊的用户信息
     */
    List<AddGroupMemberDTO> selectAddGroupMembers(@Param("groupId") Long groupId, @Param("userIds") List<Long> userIds);
}
