package com.hqy.cloud.message.es.service;

import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.service.ElasticService;
import com.hqy.cloud.message.bind.dto.MessagesRequestParamDTO;
import com.hqy.cloud.message.es.document.ImMessageDoc;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/23 13:37
 */
public interface ImMessageElasticService extends ElasticService<Long, ImMessageDoc> {

    /**
     * 分页查询
     * @param id          user id
     * @param removeTime 移除会话时间
     * @param deleteTime 会话被删除时间
     * @param params     {@link MessagesRequestParamDTO}
     * @return           result
     */
    PageResult<ImMessageDoc> queryPage(Long id, Long removeTime, Long deleteTime, MessagesRequestParamDTO params);

    /**
     * 不支持向前和随机分页.
     * 根绝search_after进行分页查询用户消息, 返回分页对象
     * @param send        发送人id
     * @param removeTime  移除时间
     * @param deleteTime  删除时间
     * @param params      参数 {@link MessagesRequestParamDTO}
     * @return            result
     */
    PageResult<ImMessageDoc> queryPageSearchAfter(Long send, Long removeTime, Long deleteTime, MessagesRequestParamDTO params);


    /**
     * query unread messages
     * @param from send message user id
     * @param to   receive message user id
     * @return     unread messages.
     */
    List<ImMessageDoc> queryUnreadMessages(Long from, Long to);
}
