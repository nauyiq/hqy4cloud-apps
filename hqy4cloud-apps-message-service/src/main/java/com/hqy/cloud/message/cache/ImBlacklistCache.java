package com.hqy.cloud.message.cache;

import com.hqy.cloud.message.bind.enums.BlacklistState;

/**
 * 黑名单缓存service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/5
 */
public interface ImBlacklistCache {

    /**
     * 判断是否拉黑了对方
     * @param firstId  第一个用户id
     * @param secondId 第二个用户id
     * @return         黑名单状态
     */
    BlacklistState getBlacklistState(Long firstId, Long secondId);

    /**
     * 添加黑名单
     * @param userId  用户id
     * @param blackId 封禁的id
     */
    void addBlacklist(Long userId, Long blackId);

    /**
     * 移除黑名单
     * @param userId  用户id
     * @param blackId 黑名单id
     */
    void removeBlacklist(Long userId, Long blackId);

}
