package com.hqy.cloud.message;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/1
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GeneratorTest {

    public static void main(String[] args) {
        DataSourceConfig.Builder builder = new DataSourceConfig
                .Builder("jdbc:mysql://120.76.65.160:3306/apps_chat_message?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true",
                "root",
                "Mysql2024.");
        DataSourceConfig dbConfig = builder.build();
        AutoGenerator autoGenerator = new AutoGenerator(dbConfig);

        // 设置全局配置
        GlobalConfig.Builder globalBuilder = new GlobalConfig.Builder();
        GlobalConfig globalConfig = globalBuilder
                // 生成的目录
                .outputDir("D:\\hqy\\code\\hqy4cloud-apps\\hqy4cloud-apps-message-service" + "/src/main/java")
                // 作者名
                .author("qiyuan.hong")
                .disableOpenDir()
                // 设置时间类型
                .dateType(DateType.ONLY_DATE).build();

        // 设置包配置
        PackageConfig.Builder packageConfigBuilder = new PackageConfig.Builder();
        PackageConfig packageConfig = packageConfigBuilder
                .entity("entity")
                .parent("com.hqy.cloud.message.db")
                .build();

        StrategyConfig.Builder strategyConfigBuilder = new StrategyConfig.Builder();
        StrategyConfig strategyConfig = strategyConfigBuilder
                .addInclude("t_im_group_conversation", "t_im_private_conversation")
                .addTablePrefix("t_im").build();


        autoGenerator.global(globalConfig).packageInfo(packageConfig).strategy(strategyConfig).execute();
    }

    @Test
    public void autoGenerator() {
        DataSourceConfig.Builder builder = new DataSourceConfig
                .Builder("jdbc:mysql://120.76.65.160:3306/apps_blog?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true",
                "root",
                "mysql2024.");
        DataSourceConfig dbConfig = builder.build();
        AutoGenerator autoGenerator = new AutoGenerator(dbConfig);

        // 设置全局配置
        GlobalConfig.Builder globalBuilder = new GlobalConfig.Builder();
        GlobalConfig globalConfig = globalBuilder
                // 生成的目录
                .outputDir("D:\\hqy\\code\\hqy4cloud-apps\\hqy4cloud-apps-message-service" + "/src/main/java")
                // 作者名
                .author("qiyuan.hong")
                // 设置时间类型
                .dateType(DateType.ONLY_DATE).build();

        // 设置包配置
        PackageConfig.Builder packageConfigBuilder = new PackageConfig.Builder();
        PackageConfig packageConfig = packageConfigBuilder
                .entity("entity")
                .parent("com.hqy.cloud.message.db")
                .moduleName("message")
                .mapper("mapper")
                .service("service")
                .serviceImpl("impl")
                .controller("controller").build();

        StrategyConfig.Builder strategyConfigBuilder = new StrategyConfig.Builder();
        StrategyConfig strategyConfig = strategyConfigBuilder
                .addInclude("t_im_friend_state", "t_im_group_message", "t_im_private_message")
                .addTablePrefix("t_im").build();


        autoGenerator.global(globalConfig).packageInfo(packageConfig).strategy(strategyConfig).execute();



    }


}
