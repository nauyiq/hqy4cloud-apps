package com.hqy.cloud.message.upload;

import com.hqy.cloud.web.config.UploadFileProperties;
import com.hqy.cloud.web.upload.UploadFileService;
import com.hqy.cloud.web.upload.support.DefaultUploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/21 16:21
 */
@Configuration
@RequiredArgsConstructor
public class ImMessageUploadConfiguration {
    private final UploadFileProperties uploadFileProperties;

    @Bean
    public UploadFileService uploadFileService() {
        return new DefaultUploadFileService(uploadFileProperties);
    }

}
