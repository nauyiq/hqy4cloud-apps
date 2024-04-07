package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.FriendDTO;
import com.hqy.cloud.message.bind.vo.FriendApplicationVO;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
public interface ImFriendRequestService {

    /**
     * 获取收到的好友申请列表
     * @param loginId 登录用户id
     * @return        申请列表
     */
    R<List<FriendApplicationVO>> getFriendApplications(Long loginId);

    /**
     * 申请添加好友
     * @param userId   当前登录用户id
     * @param friendId 好友id
     * @param remark   备注
     * @return         是否申请成功
     */
    R<Boolean> applyAddFriend(Long userId, Long friendId, String remark);

    /**
     * 同意或者拒接添加好友
     * @param accountId     账号id
     * @param applicationId 好友申请表id
     * @param status        状态，接收或者拒绝
     * @return              是否添加或者拒绝成功
     */
    R<Boolean> acceptOrRejectFriendApplication(Long accountId, Long applicationId, Boolean status);


    /**
     * 修改好友信息
     * @param accountId 用户id
     * @param friend    好友信息对象
     * @return          是否成功
     */
    R<Boolean> updateFriendInfo(Long accountId, FriendDTO friend);

    /**
     * 移除好友
     * @param accountId 用户id
     * @param friendId  好友id
     * @return          是否成功
     */
    R<Boolean> removeFriend(Long accountId, Long friendId);
}
