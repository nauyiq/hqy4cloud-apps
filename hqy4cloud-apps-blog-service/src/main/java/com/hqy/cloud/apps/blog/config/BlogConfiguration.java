package com.hqy.cloud.apps.blog.config;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.web.config.UploadFileProperties;
import com.hqy.cloud.web.upload.UploadFileService;
import com.hqy.cloud.web.upload.support.DefaultUploadFileService;
import com.hqy.cloud.web.upload.support.TencentOssCloudUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 15:14
 */
@Configuration
@RequiredArgsConstructor
public class BlogConfiguration {

    private final UploadFileProperties uploadFileProperties;

    @Bean
    public UploadFileService uploadFileService() {
        return new DefaultUploadFileService(uploadFileProperties);
    }

    @Bean
    public UploadFileService tencentUploadFileService() {
        return new TencentOssCloudUploadService(StrUtil.EMPTY, uploadFileProperties);
    }

}
