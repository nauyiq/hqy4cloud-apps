package com.hqy.cloud.message.server;

import org.quartz.JobExecutionContext;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/10/19 10:43
 */
public interface TimeListener {

    /**
     * do callback
     * @param jobExecutionContext {@link JobExecutionContext}
     */
    void callback(JobExecutionContext jobExecutionContext);


}
