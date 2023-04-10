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

    ;

    public int code;

    public String message;


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
        return null;
    }

    @Override
    public int getCode() {
        return 0;
    }
}
