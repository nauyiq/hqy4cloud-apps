package com.hqy.cloud.message.server;

import com.hqy.cloud.message.socketio.event.ContactOnlineOfflineEvent;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 15:57
 */
public interface ImEventListener {

    /**
     * 用户上下线事件
     * @param event {@link ContactOnlineOfflineEvent}
     * @return      result
     */
    boolean doContactOnlineOffline(ContactOnlineOfflineEvent event);


}
