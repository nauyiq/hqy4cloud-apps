package com.hqy.cloud.message.server.support;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.redis.key.RedisKey;
import com.hqy.cloud.foundation.redis.key.support.RedisNamedKey;
import com.hqy.cloud.message.bind.ImMessageConverter;
import com.hqy.cloud.message.es.service.ImMessageElasticService;
import com.hqy.cloud.message.tk.entity.ImMessage;
import com.hqy.cloud.message.tk.entity.ImMessageHistory;
import com.hqy.cloud.message.tk.service.ImMessageHistoryTkService;
import com.hqy.cloud.message.tk.service.ImMessageTkService;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_MESSAGE_HISTORY_DEFAULT_DAY;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/10/19 10:04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImMessageHistoryJob implements Job {
    private int historySavingDay = IM_MESSAGE_HISTORY_DEFAULT_DAY;
    private final TransactionTemplate template;
    private final ImMessageElasticService imMessageElasticService;
    private final ImMessageTkService imMessageTkService;
    private final ImMessageHistoryTkService imMessageHistoryTkService;
    private final RedisKey redisKey = new RedisNamedKey(MicroServiceConstants.MESSAGE_NETTY_SERVICE, QuartzImMessageHistorySchedulerTimerConfiguration.ID);
    private final RedissonClient redissonClient;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String key = redisKey.getKey();
        RLock lock = redissonClient.getLock(key);
        try {
            if (!lock.isLocked()) {
                lock.lock(15L, TimeUnit.SECONDS);
            }
            DateTime dateTime = DateUtil.offsetDay(new Date(), -historySavingDay);
            log.info("Do remove messages to history by datetime: {}.", DateUtil.formatDateTime(dateTime));
            List<ImMessage> messages = imMessageTkService.queryMessagesByBeforeTimes(dateTime);
            if (CollectionUtils.isNotEmpty(messages)) {
                ImMessageConverter converter = ImMessageConverter.CONVERTER;
                List<Long> messageIds = messages.parallelStream().map(ImMessage::getId).toList();
                List<ImMessageHistory> messageHistories = messages.parallelStream().map(converter::convert).toList();
                Boolean execute = template.execute(status -> {
                    try {
                        // 数据库im消息表移动到im消息历史表
                        AssertUtil.isTrue(imMessageTkService.deleteByIds(messageIds), "Failed execute to delete im messages.");
                        AssertUtil.isTrue(imMessageHistoryTkService.insertList(messageHistories), "Failed execute to insert messages to history.");
                        //删除ES数据
                        imMessageElasticService.deleteAllById(messageIds);
                        return true;
                    } catch (Throwable cause) {
                        status.setRollbackOnly();
                        log.error(cause.getMessage(), cause);
                        return false;
                    }
                });
                if (Boolean.TRUE.equals(execute)) {
                    log.info("Remove {} messages success.", messages.size());
                }
            }
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
        } finally {
            lock.unlock();
        }
    }

    public void setHistorySavingDay(int historySavingDay) {
        this.historySavingDay = historySavingDay;
    }

}
