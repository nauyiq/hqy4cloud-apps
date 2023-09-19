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
     * @param removeTime 删除时间
     * @param params     {@link MessagesRequestParamDTO}
     * @return           result
     */
    PageResult<ImMessageDoc> queryPage(Long id, Long removeTime, MessagesRequestParamDTO params);

    /**
     * query unread messages
     * @param from send message user id
     * @param to   receive message user id
     * @return     unread messages.
     */
    List<ImMessageDoc> queryUnreadMessages(Long from, Long to);
}
