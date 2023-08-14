package com.hqy.cloud.message.service;

import com.hqy.cloud.message.tk.entity.ImFriendApplication;

/**
 * 聊天好友相关操作service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 11:34
 */
public interface ImFriendOperationsService {

    /**
     * 添加好友操作
     * @param application {@link ImFriendApplication}
     * @return 添加好友
     */
    boolean addFriend(ImFriendApplication application);

    /**
     * 双方是否是好友关系
     * @param from 用户id
     * @param to   用户id
     * @return     result.
     */
    boolean isFriend(Long from, Long to);

    /**
     * 移除好友关系
     * @param from 用户id
     * @param to   用户id
     * @return     result.
     */
    boolean removeFriend(Long from, Long to);
}
