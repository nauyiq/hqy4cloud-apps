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
     * @param conversations conversation ids.
     * @return              unread map.
     */
    Map<Long, Integer> privateConversationsUnread(Long userId, List<Long> conversations);

    /**
     * add private chat conversation unread.
     * @param userId         user id.
     * @param conversationId conversation id.
     * @param offset         increase number
     */
    void addPrivateConversationUnread(Long userId, Long conversationId, Long offset);

    /**
     * remove private conversation unread.
     * @param userId         user id.
     * @param conversationId conversation id.
     */
    void readPrivateConversationUnread(Long userId, Long conversationId);


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
