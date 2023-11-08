package com.hqy.cloud.apps.blog.listener;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.apps.blog.entity.ArticleUserState;
import com.hqy.cloud.apps.blog.listener.message.ArticleStateKafkaMessage;
import com.hqy.cloud.apps.blog.service.tk.ArticleUserStateTkService;
import com.hqy.cloud.foundation.id.DistributedIdGen;
import com.hqy.cloud.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Blog.BLOG_STATE_TOPIC;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/22 13:27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleUserStateListener {

    private final ArticleUserStateTkService stateTkService;

    @KafkaListener(id = "article-user-state-consumer-group", containerFactory = "batchFactory",
            errorHandler = "articleUserStateErrorHandler", concurrency = "3", topicPartitions = {
            @TopicPartition(topic = BLOG_STATE_TOPIC, partitions = {"0"})
    })
    public void process(List<ConsumerRecord<String, String>> messages, Acknowledgment acknowledgment) {
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }
        //kafka消息序列化.
        List<ArticleStateKafkaMessage> articleStateKafkaMessages = messages.stream().map(message ->
                JsonUtil.toBean(message.value(), ArticleStateKafkaMessage.class)).toList();

        //遍历过滤重复的消息 需要统一处理的消息.
        Map<ArticleStateKafkaMessage, Long> doingMessageMap = MapUtil.newHashMap(articleStateKafkaMessages.size() / 2);
        for (ArticleStateKafkaMessage message : articleStateKafkaMessages) {
            Long value = doingMessageMap.get(message);
            if (value == null || message.getTimestamp() > value) {
                doingMessageMap.put(message, message.getTimestamp());
            }
        }
        //批量新增或修改入库.
        List<ArticleUserState> states = doingMessageMap.keySet()
                .stream()
                .map(message -> new ArticleUserState(DistributedIdGen.getSnowflakeId(), message.getArticleId(), message.getAccountId(), message.getStatus()))
                .collect(Collectors.toList());
        try {
            boolean result = stateTkService.insertOrUpdate(states);
            if (!result) {
                log.error("Failed execute to insert or update ArticleStateKafkaMessages, messages: {}.", JsonUtil.toJson(states));
            }
        } finally {
            //不管怎么样都提交ack
            acknowledgment.acknowledge();
        }


    }


    @Slf4j
    @Component(value = "articleUserStateErrorHandler")
    public static class ArticleUserStateErrorHandler implements KafkaListenerErrorHandler {
        @Override
        public Object handleError(Message<?> message, ListenerExecutionFailedException e) {
            log.error("Failed to process topic = {} cause: {}, message = {}.", BLOG_STATE_TOPIC, e.getMessage(), message.getPayload());
            return null;
        }
    }









}
