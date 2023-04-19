package com.hqy.cloud.apps.blog.es.service.impl;

import com.hqy.cloud.apps.blog.es.document.ArticleDoc;
import com.hqy.cloud.apps.blog.es.service.ArticleElasticService;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.mapper.ElasticMapper;
import com.hqy.cloud.elasticsearch.service.impl.ElasticServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
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
        //构建查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("title", title));
        }
        if (StringUtils.isNotBlank(describe)) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("intro", describe));
        }

        //根据id倒叙
        FieldSortBuilder sortBuilder = SortBuilders.fieldSort("id").order(SortOrder.DESC);
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withSorts(sortBuilder);
        return this.pageQueryByBuilder(current, size, queryBuilder);
    }

    @Override
    public Class<ArticleDoc> getDocumentClass() {
        return ArticleDoc.class;
    }
}
