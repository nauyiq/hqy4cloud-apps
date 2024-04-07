package com.hqy.cloud.message.db.service;

import com.hqy.cloud.db.mybatisplus.BasePlusService;
import com.hqy.cloud.message.bind.enums.BlacklistState;
import com.hqy.cloud.message.db.entity.Blacklist;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 黑名单表 服务类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-04
 */
public interface IBlacklistService extends BasePlusService<Blacklist> {

    /**
     * 查询黑名单状态
     * @param firstId  第一个用户ID
     * @param secondId 第二个用户ID
     * @return         黑名单状态
     */
    BlacklistState selectBlacklistState(Long firstId, Long secondId);

    /**
     * 移除黑名单
     * @param userId  用户id
     * @param blackId 黑名单id
     * @return        是否移除成功
     */
    boolean removeByUserIdAndBlackId(Long userId, Long blackId);
}
