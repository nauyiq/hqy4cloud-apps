package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.FriendDTO;
import com.hqy.cloud.message.bind.vo.FriendVO;
import com.hqy.cloud.message.bind.vo.UserImSettingVO;

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
     * 获取通讯录中的好友
     * @param id 用户id
     * @return   R.
     */
    R<List<FriendVO>> getImFriends(Long id);

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
