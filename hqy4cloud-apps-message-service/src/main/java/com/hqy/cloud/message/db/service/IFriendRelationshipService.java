package com.hqy.cloud.message.db.service;

import com.hqy.cloud.db.mybatisplus.BasePlusService;
import com.hqy.cloud.message.db.entity.FriendRelationship;

import java.util.List;

/**
 * <p>
 * 好友关系表 服务类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
public interface IFriendRelationshipService extends BasePlusService<FriendRelationship> {

    /**
     * 是否是好友
     * @param userId    用户id
     * @param anotherId 另外一个用户id
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
     * 新增或修改
     * @param relationships 好友关系
     * @return              是否新增或修改成功
     */
    boolean insertOrUpdate(List<FriendRelationship> relationships);

    /**
     * 伪删除好友关系状态
     * @param userId    用户id
     * @param friendId  好友id
     * @return          是否移除好友成功
     */
    boolean removeRelationShip(Long userId, Long friendId);


}
