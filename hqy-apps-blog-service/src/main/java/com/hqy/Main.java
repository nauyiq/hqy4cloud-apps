package com.hqy;

import com.hqy.util.spring.ProjectContextInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * start apps-blog-service.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/26 13:31
 */
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.hqy.blog.dao")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        ProjectContextInfo.startPrintf();
    }

}