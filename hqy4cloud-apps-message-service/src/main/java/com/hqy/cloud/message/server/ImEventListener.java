package com.hqy.cloud.message.server;

import com.hqy.cloud.message.bind.event.support.*;

import java.util.List;

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
    boolean onContactOnlineOffline(ContactOnlineOfflineEvent event);

    /**
     * 新增群聊事件
     * @param events {@link AddGroupEvent}
     * @return       result.
     */
    boolean onAddGroup(List<AddGroupEvent> events);

    /**
     * im私聊事件
     * @param event {@link PrivateChatEvent}
     * @return      result.
     */
    boolean onPrivateChat(PrivateChatEvent event);

    /**
     * im群聊事件
     * @param event {@link GroupChatEvent}
     * @return      result.
     */
    boolean onGroupChat(GroupChatEvent event);

    /**
     * im read messages event.
     * @param event {@link ReadMessagesEvent}
     * @return      result.
     */
    boolean onReadMessages(ReadMessagesEvent event);


}
