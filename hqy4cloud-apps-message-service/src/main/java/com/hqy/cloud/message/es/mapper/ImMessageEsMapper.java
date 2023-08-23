package com.hqy.cloud.message.es.mapper;

import com.hqy.cloud.elasticsearch.mapper.ElasticMapper;
import com.hqy.cloud.message.es.document.ImMessageDoc;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/23 13:36
 */
public interface ImMessageEsMapper extends ElasticMapper<Long, ImMessageDoc> {
}
