package com.hqy.cloud.apps.blog.es.service.impl;

import com.hqy.cloud.apps.blog.es.document.ArticleDoc;
import com.hqy.cloud.apps.blog.es.service.ArticleElasticService;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.mapper.ElasticMapper;
import com.hqy.cloud.elasticsearch.service.impl.ElasticServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/4/19 14:39
 */
@Service
public class ArticleElasticServiceImpl extends ElasticServiceImpl<Long, ArticleDoc> implements ArticleElasticService {

    public ArticleElasticServiceImpl(ElasticMapper<Long, ArticleDoc> elasticMapper, ElasticsearchTemplate elasticsearchTemplate) {
        super(elasticMapper, elasticsearchTemplate);
    }

    @Override
    public PageResult<ArticleDoc> queryPage(String title, String describe, Integer current, Integer size) {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        if (StringUtils.isNotBlank(title)) {
            queryBuilder.withQuery(q -> q.matchPhrase(m -> m.field("title").query(title)));
        }
        if (StringUtils.isNotBlank(describe)) {
            queryBuilder.withQuery(q -> q.matchPhrase(m -> m.field("intro").query(describe)));
        }
        queryBuilder.withSort(Sort.by(Sort.Direction.DESC,"id"));
        return this.pageQueryByBuilder(current, size, queryBuilder);
    }

    @Override
    public Class<ArticleDoc> getDocumentClass() {
        return ArticleDoc.class;
    }
}
