package com.hqy.cloud.apps.commom.constants;

/**
 * AppsConstants.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 15:25
 */
public interface AppsConstants {

    interface Blog {
        String UPLOAD_IMAGE_FOLDER = "/files/blog/image";
        String UPLOAD_IMAGE_MUSIC = "/files/blog/music";
        String BLOG_STATE_TOPIC = "blog-user-state-topic";
    }


    interface Message {
        String YOU = "你";
        String TARGET = "对方";
        String REPLACE = "{}";
        String IM_DEFAULT_GROUP_AVATAR_FOLDER = "/files/avatar/group";
        String IM_DEFAULT_GROUP_AVATAR = "/files/avatar/group/default_avatar.png";
        String IM_MESSAGE_INDEX= "im_message";
        String IM_MESSAGE_SUCCESS = "succeed";
        String IM_MESSAGE_FAILED = "failed";
        String ACCEPT_FRIEND_MESSAGE_CONTENT = "我通过了你的朋友验证请求，现在我们可以开始聊天了。";
        String CREATOR_GROUP_EVENT_CONTENT = "{}创建了群聊。";
        String IM_GROUP_NAME_CHANGE_CONTENT = "{}修改了群名为";
        String IM_GROUP_NOTICE_CHANGE_CONTENT = "{}发布了新的公告。";
        String UNDO_FROM_MESSAGE_CONTENT = "{}撤回了一条消息";
        Long IM_SYSTEM_MESSAGE_UNREAD_ID = -1L;
        String IM_GROUP_DEFAULT_INDEX = "[1]群聊";
    }




}
