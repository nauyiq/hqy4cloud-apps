package com.hqy.cloud.message.bind;

import com.hqy.cloud.common.base.lang.DateMeasureConstants;

/**
 * 常量类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/1
 */
public interface Constants {

    /**
     * es 聊天记录宽表索引名
     */
    String IM_MESSAGE_INDEX = "im_message_service";

    /**
     * im项目的通用线程池名字
     */
    String IM_EXECUTOR_NAME = "im_message";

    /**
     * 私聊聊天记录分布式id场景值
     */
    String IM_PRIVATE_MESSAGE_DISTRIBUTE_ID_SCENE = "private-message";

    /**
     * 群聊聊天记录分布式id场景值
     */
    String IM_GROUP_MESSAGE_DISTRIBUTE_ID_SCENE = "group-message";

    /**
     * 私聊文件上传目录
     */
    String UPLOAD_IM_PRIVATE_FOLDER = "/files/message/private";

    /**
     * 群聊文件上传目录
     */
    String UPLOAD_IM_GROUP_FOLDER = "/files/message/group";

    /**
     * 2分钟可以撤回消息
     */
    Long DEFAULT_UNDO_MESSAGE_TIMESTAMPS = DateMeasureConstants.ONE_MINUTES.toMillis() * 2;

    /**
     * 默认的最大消息转发个数
     */
    int DEFAULT_MAX_FORWARD_MESSAGE_COUNT = 5;

    /**
     * 群聊默认头像路径
     */
    String IM_DEFAULT_GROUP_AVATAR = "/files/avatar/group/default_avatar.png";

    /**
     * 占位符
     */
    String REPLACE = "{}";

    /**
     * 群聊最大人数
     */
    int GROUP_MAX_MEMBERS = 200;

    /**
     * 用于SEATA TCC阶段时获取用户信息上下文数据的key
     */
    String SEATA_TCC_IM_USER_CONTEXT = "user";

    /**
     * seata分布式事务 - 添加聊天用户
     */
    String SEATA_TRANSACTION_ADD_IM_USER = "addImUser";

    /**
     * seata分布式事务 - 同步用户信息
     */
    String SEATA_TRANSACTION_SYNC_USER_PROFILE = "syncUserProfile";




}
