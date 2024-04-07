package com.hqy.cloud.message.db.service;

import com.hqy.cloud.db.mybatisplus.BasePlusService;
import com.hqy.cloud.message.bind.dto.AddGroupMemberDTO;
import com.hqy.cloud.message.bind.dto.ImUserInfoDTO;
import com.hqy.cloud.message.db.entity.UserSetting;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * im用户设置表 服务类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-04
 */
public interface IUserSettingService extends BasePlusService<UserSetting> {

    /**
     * 根据名字查询聊天用户集合
     * @param name 名字
     * @return     用户集合
     */
    List<UserSetting> selectImUsersByName(String name);



    /**
     * 查找好友信息
     * @param id  当前登录用户
     * @param friendId 好友id集合, 包括自己
     * @return    好友信息
     */
    ImUserInfoDTO selectFriendInfoById(Long id, Long friendId);


    /**
     * 获取双方的好友信息
     * @param userId   用户id
     * @param friendId 好友id
     * @return         好友信息map
     */
    Map<Long, ImUserInfoDTO> selectFriendInfosByUserIdAndFriendId(Long userId, Long friendId);

    /**
     * 查找用户昵称
     * @param id 用户id
     * @return   昵称
     */
    String selectImUserNickname(Long id);

    /**
     * 查找用户名
     * @param ids id列表
     * @return    用户名
     */
    Map<Long, String> selectUsernames(List<Long> ids);

    /**
     * 启用聊天账号
     * @param id 账号id
     * @return   是否成功
     */
    boolean settingUserStatusEnabled(Long id);

    /**
     * 查找添加群聊成员的用户信息
     * @param groupId 群聊id
     * @param userIds 用户id列表
     * @return        添加到群聊的用户信息
     */
    List<AddGroupMemberDTO> selectAddGroupMembers(Long groupId, List<Long> userIds);

    /**
     * 同步更新聊天用户信息到账号rpc
     * @param userSetting 用户聊天信息
     * @return            是否成功
     */
    boolean syncUpdateUserInfo(UserSetting userSetting);
}
