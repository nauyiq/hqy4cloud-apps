package com.hqy.cloud.message.tk.service;

import com.hqy.cloud.db.tk.PrimaryLessTkService;
import com.hqy.cloud.message.bind.dto.ContactsDTO;
import com.hqy.cloud.message.tk.entity.ImFriend;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:15
 */
public interface ImFriendTkService extends PrimaryLessTkService<ImFriend> {

    /**
     * 移除好友
     * @param from 用户id
     * @param to   用户id
     * @return     result.
     */
    boolean removeFriend(Long from, Long to);

    /**
     * query friends by user ids
     * @param id      current user id.
     * @param userIds user ids.
     * @return        friends result
     */
    List<ImFriend> queryFriends(Long id, List<Long> userIds);

    /**
     * query contact list
     * @param userId user id.
     * @return      {@link ContactsDTO}
     */
    ContactsDTO queryContactByUserId(Long userId);

    /**
     * 根据主键更新friend
     * @param imFriend 好友entity
     * @return         是否更新成功
     */
    Boolean updateImFriend(ImFriend imFriend);
}
