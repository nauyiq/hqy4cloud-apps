package com.hqy.cloud.message.service;

import com.hqy.cloud.message.bind.enums.BlacklistState;
import com.hqy.cloud.message.bind.vo.UserInfoVO;

import java.util.List;
import java.util.Map;

/**
 * 聊天用户关系service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
public interface ImUserRelationshipService {

    /**
     * 判断两个用户是否是好友
     * @param userId    用户id
     * @param anotherId 另一个用户id
     * @return          是否是好友关系
     */
    boolean isFriend(Long userId, Long anotherId);

    /**
     * 判断是否都是朋友
     * @param userId    用户id
     * @param friendIds 好友id列表
     * @return          是否都是好友
     */
    boolean allFriend(Long userId, List<Long> friendIds);

    /**
     * 添加好友
     * @param userId           用户id
     * @param applyId          申请人ID
     * @param message          打招呼消息
     * @param applied          对方也申请过好友？
     * @return 添加好友是否成功
     */
    boolean addFriend(Long userId, Long applyId, String message, boolean applied);

    /**
     * 移除好友
     * @param userId   用户id
     * @param friendId 好友id
     * @return         是否移除成功
     */
    boolean removeFriend(Long userId, Long friendId);

    /**
     * 判断是否拉黑了对方
     * @param firstId  第一个用户id
     * @param secondId 第二个用户id
     * @return         黑名单状态
     */
    BlacklistState getBlacklistState(Long firstId, Long secondId);

    /**
     * 添加黑名单
     * @param userId  用户id
     * @param blackId 封禁的id
     * @return 是否添加黑名单成功
     */
    boolean addBlacklist(Long userId, Long blackId);

    /**
     * 移除黑名单
     * @param userId  用户id
     * @param blackId 黑名单id
     * @return 是否移除黑名单成功
     */
    boolean removeBlacklist(Long userId, Long blackId);

    /**
     * 查找好友的信息
     * @param userId    当前登录用户id
     * @param contactId 好友id
     * @return         返回map, key = 用户id； value = 用户信息
     */
    Map<Long, UserInfoVO> selectFriendMessageVO(Long userId, Long contactId);


}
