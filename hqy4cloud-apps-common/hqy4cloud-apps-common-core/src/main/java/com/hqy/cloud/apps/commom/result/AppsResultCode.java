package com.hqy.cloud.apps.commom.result;


import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.result.Result;
import com.hqy.cloud.common.result.ResultCode;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 16:01
 */
public enum AppsResultCode implements Result {

    /**
     * 无效的文章类型.
     */
    INVALID_ARTICLE_TYPE(5001, "Invalid article type."),

    /**
     * 无效的文章
     */
    INVALID_ARTICLE_ID(5002, "Invalid article id."),

    /**
     * 文章不存在
     */
    ARTICLE_NOT_FOUND(5003, "Article not exist."),

    /**
     * 评论不存在
     */
    COMMENT_NOT_FOUND(5004, "Comment not exist."),

    /**
     * 文章类型名已经存在
     */
    TYPE_EXIST(5005, "Type name already exist."),

    /**
     * 文章类型不存在.
     */
    NOT_FOUND_TYPE(5006, "Type not exist."),

    /**
     * 该用户不是您的好友
     */
    IM_NOT_FRIEND(10001, "The user is not your friend."),

    /**
     * 当前群聊已经存在
     */
    IM_GROUP_EXIST(10002, "The group already exist."),

    /**
     * 当前群聊不存在
     */
    IM_GROUP_NOT_EXIST(10003, "The group not exist."),

    /**
     * 群聊成员已满
     */
    IM_GROUP_MEMBER_COUNT_LIMITED(10004, "The im group is full."),

    /**
     * 用户不是群聊成员
     */
    IM_NOT_GROUP_MEMBER(10005, "The user is not group member."),

    /**
     * 转发的聊天不能超过五个
     */
    IM_FORWARD_SIZE_MAX(10006, "No more than five chats can be forwarded.")



    ;

    public final int code;

    public final String message;


    AppsResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static DataResponse dataResponse() {
        return dataResponse(true, ResultCode.SUCCESS, null);
    }

    public static DataResponse dataResponse(Object data) {
        return dataResponse(ResultCode.SUCCESS, data);
    }

    public static DataResponse dataResponse(Result code) {
        return dataResponse(false, code, null);
    }

    public static DataResponse dataResponse(Result code, Object data) {
        return dataResponse(true, code, data);
    }

    public static DataResponse dataResponse(boolean result, Result code, Object data) {
        return new DataResponse(result, code.getMessage(), code.getCode(), data);
    }

    public static DataResponse dataResponse(int code, String message) {
        return new DataResponse(false, message, code, null);
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
