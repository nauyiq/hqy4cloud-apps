package com.hqy.cloud.message.db.service;

import com.hqy.cloud.db.mybatisplus.BasePlusService;
import com.hqy.cloud.message.bind.dto.ContactsDTO;
import com.hqy.cloud.message.db.entity.FriendState;

import java.util.List;

/**
 * <p>
 * 好友状态表 服务类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
public interface IFriendStateService extends BasePlusService<FriendState> {

    /**
     * 查询好友状态
     * @param userId   用户id
     * @param friendId 好友id
     * @return         实体类
     */
    FriendState getByUserIdAndFriendId(Long userId, Long friendId);

    /**
     * 查找好友联系人和好友申请未读数
     * @param userId 用户id
     * @return       好友联系人
     */
    ContactsDTO queryContactsByUserId(Long userId);

    /**
     * 根据用户id查询实体列表
     * @param userId 用户id
     * @return       实体列表
     */
    List<FriendState> selectByUserId(Long userId);

    /**
     * 批量新增或更新
     * @param friendStates 好友状态列表
     * @return             是否批量新增或更新成功
     */
    boolean insertOrUpdate(List<FriendState> friendStates);

    /**
     * 伪删除好友关系表数据
     * @param userId    用户id
     * @param friendId  好友id
     * @return          是否移除好友成功
     */
    boolean removeState(Long userId, Long friendId);

    /**
     * 修改置顶状态
     * @param userId    用户id
     * @param contactId 联系人id
     * @param status    状态
     * @return          是否修改成功
     */
    boolean updateTopState(Long userId, Long contactId, Boolean status);

    /**
     * 修改通知状态
     * @param userId    用户id
     * @param contactId 联系人id
     * @param status    状态
     * @return          是否修改成功
     */
    boolean updateNoticeState(Long userId, Long contactId, Boolean status);
}
