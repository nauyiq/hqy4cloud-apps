package com.hqy.cloud.apps.blog.es.mapper;

import com.hqy.cloud.apps.blog.es.document.ArticleDoc;
import com.hqy.cloud.elasticsearch.mapper.ElasticMapper;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/4/3 10:06
 */
public interface ArticleElasticMapper extends ElasticMapper<Long, ArticleDoc> {
}
