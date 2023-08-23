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
    }




}
