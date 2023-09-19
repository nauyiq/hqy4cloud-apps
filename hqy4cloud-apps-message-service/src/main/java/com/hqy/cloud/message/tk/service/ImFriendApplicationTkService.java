package com.hqy.cloud.message.tk.service;

import com.hqy.cloud.db.tk.BaseTkService;
import com.hqy.cloud.message.bind.dto.FriendApplicationDTO;
import com.hqy.cloud.message.tk.entity.ImFriendApplication;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:24
 */
public interface ImFriendApplicationTkService extends BaseTkService<ImFriendApplication, Long> {

    /**
     * 新增或更新
     * @param application entity
     * @return            result
     */
    boolean insertDuplicate(ImFriendApplication application);

    /**
     * 查询用户的好友申请列表
     * @param userId 用户id
     * @return       好友申请列表
     */
    List<ImFriendApplication> queryFriendApplications(Long userId);

    /**
     * 批量更新申请列表状态
     * @param ids    ids
     * @param status 状态值
     * @return       是否更新成功
     */
    boolean updateApplicationStatus(List<Long> ids, int status);

    /**
     * 查找好友申请状态
     * @param id     当前用户id
     * @param userId 被申请用户id
     * @return       {@link FriendApplicationDTO}
     */
    FriendApplicationDTO queryApplicationStatus(Long id, Long userId);
}
