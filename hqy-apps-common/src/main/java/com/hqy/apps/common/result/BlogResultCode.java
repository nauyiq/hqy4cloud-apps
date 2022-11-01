package com.hqy.apps.common.result;

import com.hqy.base.common.bind.DataResponse;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 16:01
 */
public enum BlogResultCode {

    /**
     * success.
     */
    SUCCESS(0, "success."),

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

    ;

    public int code;

    public String message;


    BlogResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static DataResponse dataResponse() {
        return dataResponse(true, BlogResultCode.SUCCESS, null);
    }

    public static DataResponse dataResponse(Object data) {
        return dataResponse(BlogResultCode.SUCCESS, data);
    }

    public static DataResponse dataResponse(BlogResultCode code) {
        return dataResponse(false, code, null);
    }

    public static DataResponse dataResponse(BlogResultCode code, Object data) {
        return dataResponse(true, code, data);
    }

    public static DataResponse dataResponse(boolean result, BlogResultCode code, Object data) {
        return new DataResponse(result, code.message, code.code, data);
    }

    public static DataResponse dataResponse(int code, String message) {
        return new DataResponse(false, message, code, null);
    }

}
