package com.hqy.cloud.message.tk.service;

import com.hqy.cloud.db.tk.PrimaryLessTkService;
import com.hqy.cloud.message.tk.entity.ImFriendApplication;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:24
 */
public interface ImFriendApplicationTkService extends PrimaryLessTkService<ImFriendApplication> {

    /**
     * 新增或更新
     * @param application entity
     * @return            rows
     */
    int insertDuplicate(ImFriendApplication application);

    /**
     * 查询用户的好友申请列表
     * @param userId 用户id
     * @return       好友申请列表
     */
    List<ImFriendApplication> queryFriendApplications(Long userId);
}
