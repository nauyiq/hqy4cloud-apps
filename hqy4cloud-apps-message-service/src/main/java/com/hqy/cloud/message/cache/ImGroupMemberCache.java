package com.hqy.cloud.message.cache;

import java.util.Set;

/**
 * 群聊成员缓存类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/5
 */
public interface ImGroupMemberCache {

    /**
     * 是否是群聊成员
     * @param id      user id
     * @param groupId group id
     * @return        result
     */
    Boolean isGroupMember(Long id, Long groupId);

    /**
     * 添加群聊成员
     * @param id      user id
     * @param groupId group id
     */
    void addGroupMember(Long id, Long groupId);

    /**
     * 移除群聊成员
     * @param id      用户id
     * @param groupId 群聊id
     */
    void removeGroupMember(Long id, Long groupId);

    /**
     * 移除所有群聊成员
     * @param groupId 群聊id
     */
    void removeGroupAllMembers(Long groupId);

    /**
     * 获取群聊成员
     * @param groupId 群聊id
     * @return        群聊成员
     */
    Set<Long> getGroupMembers(Long groupId);

    /**
     * 批量添加群聊成员
     * @param groupId 群聊id
     * @param members 群聊成员
     */
    void addGroupMembers(Long groupId, Set<Long> members);


}
