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
        String UPLOAD_IM_PRIVATE_FOLDER = "/files/message/private";
        String UPLOAD_IM_GROUP_FOLDER = "/files/message/group";
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
        String IM_GROUP_REMOVE_MEMBER_CONTENT = "{}被移除了群聊。";
        String IM_GROUP_DELETE_CONTENT = "群主解散了该群聊";
        String IM_UNDO_MESSAGE_CONTENT = "{}撤回了一条消息";
        String IM_FROM_UNDO_MESSAGE_CONTENT = "你撤回了一条消息";
        String IM_TO_UNDO_MESSAGE_CONTENT = "撤回了一条消息";
        String IM_PRIVATE_TO_UNDO_MESSAGE_CONTENT = TARGET.concat(IM_TO_UNDO_MESSAGE_CONTENT);

        Long IM_SYSTEM_MESSAGE_UNREAD_ID = -1L;
        Long IM_FILE_MESSAGE_MEX_SIZE = 5 * 1024 * 1000L;
        String IM_GROUP_DEFAULT_INDEX = "[1]群聊";
        int IM_MESSAGE_HISTORY_DEFAULT_DAY = 60;
//        String IM_MESSAGE_HISTORY_DEFAULT_CRON = "0 0/1 * * * ? *";
        String IM_MESSAGE_HISTORY_DEFAULT_CRON = "0 0 3 * * ? *";
    }




}
