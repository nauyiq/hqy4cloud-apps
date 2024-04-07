package com.hqy.cloud.message.server.support;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_MESSAGE_HISTORY_DEFAULT_CRON;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/10/19 9:43
 */
@Slf4j
@Configuration
public class QuartzImMessageHistorySchedulerTimerConfiguration {
    public static final String ID = "ImMessage-history";


    /*@Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(ImMessageHistoryJob.class)
                .withIdentity(QuartzImMessageHistorySchedulerTimerConfiguration.ID)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger() {
        CronScheduleBuilder cron =
                CronScheduleBuilder.cronSchedule(IM_MESSAGE_HISTORY_DEFAULT_CRON);
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity(ID.concat("-trigger"))
                .withSchedule(cron)
                .build();
    }*/




}
