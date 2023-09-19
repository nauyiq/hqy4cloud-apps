package com.hqy.cloud.message.service;

import java.util.List;
import java.util.Map;

/**
 * 聊天好友相关操作service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 11:34
 */
public interface ImFriendOperationsService {

    /**
     * 添加好友
     * @param apply   申请用户id
     * @param receive 接收用户id
     * @param remark  备注
     * @return        是否添加成功
     */
    boolean addFriend(Long apply, Long receive, String remark);

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

    /**
     * 获取好友备注列表
     * @param id        用户id
     * @param friendIds 好友id列表
     * @return          好友备注, key:userId value:好友备注
     */
    Map<Long, String> getFriendRemarks(Long id, List<Long> friendIds);



}
