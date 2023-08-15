package com.hqy.cloud.message.socketio.event;

import lombok.AllArgsConstructor;

/**
 * 用户上下线事件
 *
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 15:53
 */
@AllArgsConstructor
public record ContactOnlineOfflineEvent(Long id, Boolean online) {
    public static final String EVENT = "onlineOffline";

    @Override
    public Long id() {
        return id;
    }

    @Override
    public Boolean online() {
        return online;
    }

}
