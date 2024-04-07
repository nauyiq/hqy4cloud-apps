package com.hqy.cloud.message.db.service;

import com.hqy.cloud.db.mybatisplus.BasePlusService;
import com.hqy.cloud.message.bind.dto.FriendApplicationDTO;
import com.hqy.cloud.message.db.entity.FriendApplication;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/7
 */
public interface IFriendApplicationService extends BasePlusService<FriendApplication> {

    /**
     * 根据接收人和申请人查询
     * @param apply   申请人id
     * @param receive 接收人id
     * @return        申请实体
     */
    FriendApplication getByApplyAndReceive(Long apply, Long receive);

    /**
     * 根据用户id查找此用户接收到的好友申请列表
     * @param userId 用户id
     * @return       好友申请列表
     */
    List<FriendApplicationDTO> queryApplicationByUserId(Long userId);

    /**
     * 判断好友请求是否存在， 并且状态还是有效的. 即状态还未过期、还未接收或未拒接
     * @param applyId   申请人ID
     * @param receiveId 接收人ID
     * @return          是否存在
     */
    boolean checkIsRequestApplicationAndStateIsValid(Long applyId, Long receiveId);

    /**
     * 新增或更新好友申请列表
     * @param applications 好友申请列表
     * @return             是否新增或更新成功
     */
    boolean insertOrUpdate(List<FriendApplication> applications);

    /**
     * 根据id批量修改状态
     * @param ids    id集合
     * @param status 状态
     */
    void updateApplicationsStatus(List<Long> ids, Integer status);

    /**
     * 根据申请人id和接收人id修改状态
     * @param apply     申请人id
     * @param receiver  接收人id
     * @param status    状态
     */
    void updateApplicationStatusByApplyAndReceive(Long apply, Long receiver, Integer status);


}
