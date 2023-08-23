package com.hqy.cloud.message.es.service;

import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.service.ElasticService;
import com.hqy.cloud.message.bind.dto.MessagesRequestParamDTO;
import com.hqy.cloud.message.es.document.ImMessageDoc;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/23 13:37
 */
public interface ImMessageElasticService extends ElasticService<Long, ImMessageDoc> {

    /**
     * 分页查询
     * @param params {@link MessagesRequestParamDTO}
     * @return       result
     */
    PageResult<ImMessageDoc> queryPage(MessagesRequestParamDTO params);
}
