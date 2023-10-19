package com.hqy.cloud.message.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * cache message and conversation of unread service.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/28 10:57
 */
public interface ImUnreadCacheService {

    /**
     * return private chat conversation unread.
     * @param userId        user id.
     * @param toContactId   to ids.
     * @return              unread map.
     */
    Map<Long, Integer> privateConversationsUnread(Long userId, List<Long> toContactId);

    /**
     * add private chat conversation unread.
     * @param userId         user id.
     * @param toContactId    contact id.
     * @param offset         increase number
     */
    void addPrivateConversationUnread(Long userId, Long toContactId, Long offset);

    /**
     * add private chat conversation unread.
     * @param userId         user id.
     * @param unreadContacts contacts unread
     */
    void addPrivateConversationsUnread(Long userId, Map<Long, Long> unreadContacts);

    /**
     * add private chat conversation unread.
     * @param userId         user id.
     * @param contacts       contacts
     */
    void addPrivateConversationsUnread(Long userId, List<Long> contacts);

    /**
     * add private chat conversation unread by ids.
     * @param userIds user ids
     * @param contact contact id
     */
    void addPrivateConversationsUnreadByUserIds(List<Long> userIds, Long contact);


    /**
     * remove private conversation unread.
     * @param userId         user id.
     * @param toContactId    contact id.
     */
    void readPrivateConversationUnread(Long userId, Long toContactId);

    /**
     * return private user conversation unread.
     * @param userId         user id.
     * @param toContactId    contact id.
     * @return               unread message count.
     */
    Integer getPrivateConversationUnread(Long userId, Long toContactId);


    /**
     * return group chat conversation unread.
     * @param userId    user id.
     * @param groupIds  group ids.
     * @return          unread map
     */
    Map<Long, Integer> groupConversationsUnread(Long userId, List<Long> groupIds);


    /**
     * add group chat conversation unread.
     * @param userId  user id.
     * @param groupId group id.
     * @param offset  increase number
     */
    void addGroupConversationUnread(Long userId, Long groupId, Long offset);

    /**
     * 批量新增群聊成员未读消息
     * @param userIds 群聊成员ids
     * @param groupId 群聊id
     * @param offset  未读消息数目.
     */
    void addGroupConversationsUnread(Set<Long> userIds, Long groupId, Long offset);

    /**
     * remove group conversation unread.
     * @param userId  user id.
     * @param groupId group id.
     */
    void readGroupConversationUnread(Long userId, Long groupId);










}
