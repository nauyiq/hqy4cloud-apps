package com.hqy.cloud.message.db.mapper;

import com.hqy.cloud.db.mybatisplus.BasePlusMapper;
import com.hqy.cloud.message.bind.dto.ContactDTO;
import com.hqy.cloud.message.bind.dto.ContactsDTO;
import com.hqy.cloud.message.db.entity.FriendState;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 好友状态表 Mapper 接口
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
public interface FriendStateMapper extends BasePlusMapper<FriendState> {

    /**
     * 根据用户id查询实体列表
     * @param userId 用户id
     * @return       实体列表
     */
    List<FriendState> selectByUserId(@Param("userId") Long userId);

    /**
     * 查找好友联系人和好友申请未读数
     * @param userId 用户id
     * @return       好友联系人
     */
    List<ContactDTO> queryContactsByUserId(@Param("userId") Long userId);

    /**
     * 根据好友id查询实体列表
     * @param friendId 好友id|接受人id
     * @return         实体列表
     */
//    List<FriendState> selectByFriendId(@Param("friendId") Long friendId);

    /**
     * 批量新增或更新
     * @param friendStates 好友状态列表
     * @return             行数
     */
    int insertOrUpdate(@Param("friendStates") List<FriendState> friendStates);

    /**
     * 移除好友状态
     * @param userId    用户id
     * @param friendId  好友id
     * @return          行数
     */
    int removeState(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * 修改置顶状态
     * @param userId    用户id
     * @param contactId 联系人id
     * @param status    状态
     * @return          是否修改成功
     */
    int updateTopState(@Param("userId") Long userId, @Param("contactId") Long contactId, @Param("status") Boolean status);

    /**
     * 修改通知状态
     * @param userId    用户id
     * @param contactId 联系人id
     * @param status    状态
     * @return          是否修改成功
     */
    int updateNoticeState(@Param("userId") Long userId, @Param("contactId") Long contactId, @Param("status") Boolean status);

    /**
     * 查询好友申请未读消息数
     * @param userId 用户id
     * @return       好友申请未读消息数
     */
    Integer queryApplicationUnread(@Param("userId") Long userId);
}
