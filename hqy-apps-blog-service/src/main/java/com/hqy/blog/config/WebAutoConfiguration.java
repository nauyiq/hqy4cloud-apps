package com.hqy.blog.config;

import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.web.service.UploadFileService;
import com.hqy.web.service.support.DefaultUploadFileService;
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
        return new DefaultUploadFileService(StringConstants.Host.HTTPS +  StringConstants.Host.FILE_HQY_HOST);
    }

}
