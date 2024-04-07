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
     * 群聊事件消息，基于不同的消息发给不同的人
     * @param event 群聊事件消息事件
     * @return      是否成功
     */
    boolean onMessageEventGroupChat(MessageEventGroupChatEvent event);

    /**
     * im read messages event.
     * @param event {@link ReadMessagesEvent}
     * @return      result.
     */
    boolean onReadMessages(ReadMessagesEvent event);

    /**
     * set chat top status event.
     * @param event {@link ImTopChatEvent}
     * @return      result.
     */
    boolean onImTopChatEvent(ImTopChatEvent event);

    /**
     * set chat notice status event.
     * @param event {@link ImNoticeChatEvent}
     * @return      result.
     */
    boolean onImNoticeChatEvent(ImNoticeChatEvent event);

    /**
     * append im chat event.
     * @param event {@link AppendChatEvent}
     * @return       result.
     */
    boolean onImAppendPrivateChatEvent(AppendChatEvent event);

    /**
     * 新增群聊事件
     * @param events {@link AddGroupEvent}
     * @return       result.
     */
    boolean onImAppendGroupChatEvent(List<AppendChatEvent> events);

    /**
     * 申请添加好友事件
     * @param event {@link FriendApplicationEvent}
     * @return      result.
     */
    boolean onAddFriendApplicationEvent(FriendApplicationEvent event);

    /**
     * undo message event.
     * @param event {@link UndoMessageEvent}
     * @return      result.
     */
    boolean onImUndoMessageEvent(UndoMessageEvent event);

    /**
     * 联系人名称变更事件
     * @param event {@link ContactNameChangeEvent}
     * @return      result.
     */
    boolean onContactNameChangeEvent(ContactNameChangeEvent event);

    /**
     * 群公告变更事件
     * @param groupNoticeEvent {@link GroupNoticeEvent}
     * @return                 result.
     */
    boolean onGroupNoticeChangeEvent(GroupNoticeEvent groupNoticeEvent);

    /**
     * 移除群成员事件
     * @param event {@link RemoveGroupMemberEvent}
     * @return      result.
     */
    boolean onRemoveGroupMemberEvent(RemoveGroupMemberEvent event);

    /**
     * 退出群聊事件
     * @param event {@link ExitGroupEvent}
     * @return      result.
     */
    boolean onExitGroupMemberEvent(ExitGroupEvent event);

    /**
     * 添加群成员事件
     * @param event {@link AddGroupMemberEvent}
     * @return      result.
     */
    boolean onAddGroupMemberEvent(AddGroupMemberEvent event);

    /**
     * 删除群聊事件
     * @param event {@link DeleteGroupEvent}
     * @return      result.
     */
    boolean onDeleteGroupEvent(DeleteGroupEvent event);
}
