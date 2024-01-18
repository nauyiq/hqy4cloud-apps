package com.hqy.cloud.apps.blog.es.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
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

import java.util.ArrayList;
import java.util.List;

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
        List<Query> mustQueries = new ArrayList<>(2);
        mustQueries.add(QueryBuilders.term(m -> m.field("deleted").value(Boolean.FALSE)));
        if (StringUtils.isNotBlank(title)) {
            mustQueries.add(QueryBuilders.matchPhrase(m -> m.field("title").query(title)));
        }
        if (StringUtils.isNotBlank(describe)) {
            mustQueries.add(QueryBuilders.matchPhrase(m -> m.field("intro").query(describe)));
        }
        queryBuilder.withSort(Sort.by(Sort.Direction.DESC,"created"));
        queryBuilder.withQuery(q -> q.bool(b -> b.must(mustQueries)));
        return this.pageQueryByBuilder(current, size, queryBuilder);
    }

    @Override
    public Class<ArticleDoc> getDocumentClass() {
        return ArticleDoc.class;
    }
}
