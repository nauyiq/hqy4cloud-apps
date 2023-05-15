package com.hqy.cloud.apps.blog.config;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.web.service.UploadFileService;
import com.hqy.cloud.web.service.support.DefaultUploadFileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 15:14
 */
@Configuration
public class WebAutoConfiguration {

    @Bean
    public UploadFileService uploadFileService() {
        return new DefaultUploadFileService(StringConstants.Host.HTTPS +  StringConstants.Host.FILE_HQY_HOST, 10 * 1000 * 1024);
    }

}
