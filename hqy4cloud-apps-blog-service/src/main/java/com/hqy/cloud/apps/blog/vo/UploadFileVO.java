package com.hqy.cloud.apps.blog.vo;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * UploadFileVO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 16:17
 */
@Data
public class UploadFileVO {

    private String path;
    private String relativePath;
    private String fileName;

    public UploadFileVO(String path, String relativePath) {
        this.path = path;
        this.relativePath = relativePath;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("path", path)
                .append("relativePath", relativePath)
                .toString();
    }
}
