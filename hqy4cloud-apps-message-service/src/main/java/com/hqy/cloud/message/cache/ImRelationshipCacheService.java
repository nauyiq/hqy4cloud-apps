package com.hqy.cloud.message.cache;

import java.util.List;
import java.util.Map;

/**
 * ImRelationshipCacheService.
 * cache friend relationship and friend or group remark.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/28 17:17
 */
public interface ImRelationshipCacheService {

    /**
     * add friend relationship
     * @param from     user id
     * @param to       user id
     * @param remark   friend remark
     * @return         result.
     */
    Boolean addFriendRelationship(Long from, Long to, String remark);

    /**
     * add friends relationship
     * @param userId       user id.
     * @param relationship friend relationship
     */
    void addFriendsRelationship(Long userId, Map<Long, String> relationship);

    /**
     * add group member relationship
     * @param groupId group id
     * @param userId  user id
     * @param remark  group member remark.
     */
    void addGroupMemberRelationship(Long groupId, Long userId, String remark);

    /**
     * add group members relationship
     * @param groupId group id
     * @param remarks group member remark
     */
    void addGroupMembersRelationship(Long groupId, Map<Long, String> remarks);


    /**
     * return friend remark
     * @param userId   user id.
     * @param friendId friend id
     * @return         friend remark.
     */
    String getFriendRemark(Long userId, Long friendId);

    /**
     * return friends remark
     * @param userId    user id
     * @param friendIds friend ids
     * @return          friends remark.
     */
    List<String> getFriendRemarks(Long userId, List<Long> friendIds);

    /**
     * return group members in group remark.
     * @param groupId      group id.
     * @param groupMembers group member ids.
     * @return             group member remark.
     */
    List<String> getGroupRemarks(Long groupId, List<Long> groupMembers);


    /**
     * return users is friend.
     * @param from userId
     * @param to   userId
     * @return     is friend result.
     */
    Boolean isFriend(Long from, Long to);

    /**
     * check member is group member.
     * @param groupId  group id.
     * @param memberId member id.
     * @return         is group member result.
     */
    Boolean isGroupMember(Long groupId, Long memberId);

    /**
     * 移除群聊关系
     * @param groupId  群聊id
     * @param memberId 用户id
     * @return         result.
     */
    Boolean removeGroupMember(Long groupId, Long memberId);

    /**
     * 移除群聊关系
     * @param groupId 群id
     * @return        result
     */
    Boolean removeGroup(Long groupId);

    /**
     * delete friend relationship
     * @param from user id.
     * @param to   user id.
     * @return     result.
     */
    Boolean removeFriend(Long from, Long to);






}
