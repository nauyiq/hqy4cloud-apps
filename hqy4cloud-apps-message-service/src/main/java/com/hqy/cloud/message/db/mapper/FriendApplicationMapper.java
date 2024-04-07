package com.hqy.cloud.message.db.mapper;

import com.hqy.cloud.db.mybatisplus.BasePlusMapper;
import com.hqy.cloud.message.bind.dto.FriendApplicationDTO;
import com.hqy.cloud.message.db.entity.FriendApplication;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/7
 */
public interface FriendApplicationMapper extends BasePlusMapper<FriendApplication> {

    /**
     * 根据用户id查找此用户接收到的好友申请列表
     * @param userId 用户id
     * @return       好友申请列表
     */
    List<FriendApplicationDTO> queryApplicationByUserId(@Param("userId") Long userId);

    /**
     * 新增或更新
     * @param applications 申请列表
     * @return             行数
     */
    int insertOrUpdate(@Param("applications") List<FriendApplication> applications);

    /**
     * 根据id批量修改状态
     * @param ids    id集合
     * @param status 状态
     */
    void updateApplicationsStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 根据申请人id和接收人id修改状态
     * @param apply     申请人id
     * @param receiver  接收人id
     * @param status    状态
     */
    void updateApplicationStatusByApplyAndReceive(@Param("apply") Long apply, @Param("receiver") Long receiver, @Param("status") Integer status);
}
