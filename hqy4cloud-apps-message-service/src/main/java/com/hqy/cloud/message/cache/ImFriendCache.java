package com.hqy.cloud.message.cache;

/**
 * 好友状态缓存类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/1
 */
public interface ImFriendCache {

    /**
     * 判断两个人是否是好友
     * @param userId   用户id
     * @param friendId 朋友id
     * @return         是否是好友
     */
    boolean isFriend(Long userId, Long friendId);

    /**
     * 添加好友
     * @param userId    用户id
     * @param friendId  好友id
     */
    void addFriend(Long userId, Long friendId);

    /**
     * 移除好友
     * @param userId    用户id
     * @param friendId  好友id
     */
    void removeFriend(Long userId, Long friendId);
}
