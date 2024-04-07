package com.hqy.cloud.message.bind;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/12
 */
public interface PropertiesConstants {

    String YOUR_KEY = "im.you";
    String TARGET_KEY = "im.target";
    String GROUP_NAME_DEFAULT_PREFIX_KEY = "im.group.name.default.prefix";

    String GROUP_INDEX_KEY = "im.group.index";




    /**
     * 添加好友时的消息
     */
    String ADD_FRIEND_MESSAGE_KEY = "im.add.friend";

    /**
     * 自己视角撤回消息
     */
    String UNDO_MESSAGE_BY_YOURSELF = "im.message.undo.self";

    /**
     * 好友视角撤回消息
     */
    String UNDO_MESSAGE_BY_FRIEND = "im.message.undo.friend";

    /**
     * 群聊用户撤回消息
     */
    String UNDO_MESSAGE_BY_GROUP = "im.message.undo.group";

    /**
     * 添加好友时，默认的申请消息
     */
    String ADD_FRIEND_DEFAULT_APPLY_KEY = "im.add.friend.default.apply";

    /**
     * 添加好友成功时的提示消息
     */
    String ADD_FRIEND_NOTICE_KEY = "im.add.friend.notice";



}
