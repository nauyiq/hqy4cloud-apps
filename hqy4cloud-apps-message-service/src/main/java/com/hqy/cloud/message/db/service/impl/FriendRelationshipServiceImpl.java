package com.hqy.cloud.message.db.service.impl;

import com.hqy.cloud.db.mybatisplus.BasePlusServiceImpl;
import com.hqy.cloud.message.db.entity.FriendRelationship;
import com.hqy.cloud.message.db.mapper.FriendRelationshipMapper;
import com.hqy.cloud.message.db.service.IFriendRelationshipService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 好友关系表 服务实现类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
@Service
public class FriendRelationshipServiceImpl extends BasePlusServiceImpl<FriendRelationship, FriendRelationshipMapper> implements IFriendRelationshipService {

    @Override
    public boolean isFriend(Long userId, Long anotherId) {
        List<Long> ids = baseMapper.queryIdsByUserIdAndFriendId(userId, anotherId);
        return CollectionUtils.isNotEmpty(ids);
    }

    @Override
    public boolean allFriend(Long userId, List<Long> friendIds) {
        List<FriendRelationship> relationships = baseMapper.queryIdsByUserIdAndFriendIds(userId, friendIds);
        if (CollectionUtils.isEmpty(relationships)) {
            return false;
        }
        // 双层遍历判断是否是好友
        return friendIds.stream().allMatch(friendId -> relationships.stream().anyMatch(relationship -> {
           Long apply = relationship.getApply();
           Long receive = relationship.getReceive();
           return (apply.equals(userId) && receive.equals(friendId)) || (receive.equals(userId) && apply.equals(friendId));
       }));
    }

    @Override
    public boolean insertOrUpdate(List<FriendRelationship> relationships) {
        return baseMapper.insertOrUpdate(relationships) > 0;
    }

    @Override
    public boolean removeRelationShip(Long userId, Long friendId) {
        return baseMapper.removeRelationShip(userId, friendId) > 0;
    }
}
