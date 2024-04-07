package com.hqy.cloud.message.db.mapper;

import com.hqy.cloud.db.mybatisplus.BasePlusMapper;
import com.hqy.cloud.message.db.entity.FriendRelationship;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 好友关系表 Mapper 接口
 * </p>
 * @author qiyuan.hong
 * @since 2024-03-01
 */
public interface FriendRelationshipMapper extends BasePlusMapper<FriendRelationship> {

    /**
     * 根据两个id查询是否对应id集合
     * @param userId    用户id
     * @param anotherId 另一个id
     * @return          id集合
     */
    List<Long> queryIdsByUserIdAndFriendId(@Param("userId") Long userId, @Param("anotherId") Long anotherId);

    /**
     * 查找好友关系表id集合
     * @param userId    用户id
     * @param friendIds 好友id列表
     * @return          id集合
     */
    List<FriendRelationship> queryIdsByUserIdAndFriendIds(@Param("userId") Long userId, @Param("friendIds") List<Long> friendIds);

    /**
     * 批量新增或更新
     * @param relationships 实体对象集合
     * @return              行数
     */
    int insertOrUpdate(@Param("relationships") List<FriendRelationship> relationships);

    /**
     * 伪删除好友关系状态
     * @param userId    用户id
     * @param friendId  好友id
     * @return          行数
     */
    int removeRelationShip(@Param("userId") Long userId, @Param("friendId") Long friendId);


}
