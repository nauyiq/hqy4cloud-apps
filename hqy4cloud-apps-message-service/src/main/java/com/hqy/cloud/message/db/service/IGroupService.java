package com.hqy.cloud.message.db.service;

import com.hqy.cloud.db.mybatisplus.BasePlusService;
import com.hqy.cloud.message.db.entity.Group;

/**
 * <p>
 * im群聊表 服务类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-05
 */
public interface IGroupService extends BasePlusService<Group> {

    /**
     * 伪删除群聊
     * @param groupId 群聊id
     * @return        是否删除成功
     */
    boolean deletedGroup(Long groupId);
}
