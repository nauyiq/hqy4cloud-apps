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
        String IM_MESSAGE_INDEX= "im_message";
        String IM_MESSAGE_SUCCESS = "succeed";
        String IM_MESSAGE_FAILED = "failed";
        String ACCEPT_FRIEND_MESSAGE_CONTENT = "我通过了你的朋友验证请求，现在我们可以开始聊天了";
        String UNDO_FROM_MESSAGE_CONTENT = "你撤回了一条消息";
        String UNDO_TO_MESSAGE_CONTENT = "对方撤回了一条消息";
    }




}
