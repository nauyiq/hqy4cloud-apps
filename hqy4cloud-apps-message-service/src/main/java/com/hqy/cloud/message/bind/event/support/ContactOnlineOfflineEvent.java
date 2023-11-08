package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.event.ImEvent;

/**
 * 用户上下线事件
 *
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 15:53
 */
public record ContactOnlineOfflineEvent(Long id, Boolean status) implements ImEvent {

    @Override
    public String name() {
        return "ContactOnlineOfflineEvent";
    }
}
