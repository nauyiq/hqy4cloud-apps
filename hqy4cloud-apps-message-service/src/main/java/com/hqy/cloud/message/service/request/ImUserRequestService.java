package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.FriendDTO;
import com.hqy.cloud.message.bind.vo.*;

import java.util.List;

/**
 * @author qiyuan.hong
 * @date 2023-08-12 12:00
 */
public interface ImUserRequestService {

    /**
     * 获取用户聊天设置
     * @param  id 用户id
     * @return R.
     */
    R<UserImSettingVO> getUserImSetting(Long id);

    /**
     * update user im setting.
     * @param userId  user id.
     * @param setting request params {@link UserImSettingVO}
     * @return R.
     */
    R<Boolean> updateUserImSetting(Long userId, UserImSettingVO setting);

    /**
     * 获取通讯录中的好友
     * @param id 用户id
     * @return   R.
     */
    R<List<FriendVO>> getImFriends(Long id);

    /**
     * return im userInfo by user id.
     * @param id     current user id.
     * @param userId search user id.
     * @return       R.
     */
    R<UserCardVO> getImUserCardInfo(Long id, Long userId);

    /**
     * 根据用户名或昵称查询用户
     * @param id   当前用户id
     * @param name 用户名或昵称
     * @return     R.
     */
    R<List<UserInfoVO>> searchImUsers(Long id, String name);

    /**
     * 获取通讯录列表
     * @param userId 用户id
     * @return {@link ContactsVO}
     */
    R<ContactsVO> getUserImContacts(Long userId);

    /**
     * 分页查询好友申请列表
     * @param userId     用户id
     * @return           R.
     */
    R<List<UserApplicationVO>> queryApplications(Long userId);

    /**
     * 申请添加用户
     * @param id  用户id
     * @param add {@link FriendDTO}
     * @return    R.
     */
    R<Boolean> addImFriend(Long id, FriendDTO add);

    /**
     * 同意或者拒绝添加好友申请
     * @param id        登录用户id
     * @param friendDTO {@link FriendDTO}
     * @return          R.
     */
    R<Boolean> acceptOrRejectImFriend(Long id, FriendDTO friendDTO);

    /**
     * 修改好友备注
     * @param id     登录用户id
     * @param userId 好友id
     * @param mark   备注
     * @return       R.
     */
    R<Boolean> updateFriendMark(Long id, Long userId, String mark);

    /**
     * 移除好友
     * @param id     登录用户id
     * @param userId 好友id
     * @return       R.
     */
    R<Boolean> removeFriend(Long id, Long userId);



}
