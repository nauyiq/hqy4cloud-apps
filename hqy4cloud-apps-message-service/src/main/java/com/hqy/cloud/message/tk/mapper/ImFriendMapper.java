package com.hqy.cloud.message.tk.mapper;

import com.hqy.cloud.db.tk.PrimaryLessTkMapper;
import com.hqy.cloud.message.bind.dto.ContactsDTO;
import com.hqy.cloud.message.tk.entity.ImFriend;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:14
 */
@Repository
public interface ImFriendMapper extends PrimaryLessTkMapper<ImFriend> {

    /**
     * 移除好友
     * @param from 用户id
     * @param to   用户id
     * @return     result.
     */
    int removeFriend(@Param("from") Long from, @Param("to") Long to);

    /**
     * query friends by user ids
     * @param id      current user id.
     * @param userIds user ids.
     * @return        friends result
     */
    List<ImFriend> queryFriends(@Param("id") Long id, @Param("userIds") List<Long> userIds);

    /**
     * 获取用户通讯录列表
     * @param userId 用户id
     * @return       {@link ContactsDTO}
     */
    ContactsDTO queryUserContacts(@Param("id") Long userId);

    /**
     * 根据主键更新friend
     * @param imFriend 好友entity
     * @return         是否更新成功
     */
    int updateImFriend(@Param("friend")ImFriend imFriend);
}
