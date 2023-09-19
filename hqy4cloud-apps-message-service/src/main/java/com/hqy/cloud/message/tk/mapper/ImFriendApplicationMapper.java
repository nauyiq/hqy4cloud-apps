package com.hqy.cloud.message.tk.mapper;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.message.bind.dto.FriendApplicationDTO;
import com.hqy.cloud.message.tk.entity.ImFriendApplication;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:23
 */
@Repository
public interface ImFriendApplicationMapper extends BaseTkMapper<ImFriendApplication, Long> {

    /**
     * 新增或更新
     * @param application entity
     * @return            rows
     */
    int insertDuplicate(@Param("application") ImFriendApplication application);

    /**
     * 查询用户的申请列表
     * @param userId 用户id
     * @return       用户好友申请列表，包括申请的和接收到的
     */
    List<ImFriendApplication> queryFriendApplications(@Param("userId") Long userId);

    /**
     * 批量更新申请列表状态
     * @param ids    ids
     * @param status 状态值
     * @return       是否更新成功
     */
    int updateApplicationStatus(@Param("ids") List<Long> ids, @Param("status") int status);

    /**
     * 查询未读消息数
     * @param userId 接收人id
     * @return       未读消息数
     */
    int selectUnread(@Param("receive") Long userId);

}
