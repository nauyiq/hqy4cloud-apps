package com.hqy.cloud.message.es.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.mapper.ElasticMapper;
import com.hqy.cloud.elasticsearch.service.impl.ElasticServiceImpl;
import com.hqy.cloud.message.bind.dto.MessagesRequestParamDTO;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.es.document.ImMessageDoc;
import com.hqy.cloud.message.es.service.ImMessageElasticService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/23 13:37
 */
@Service
public class ImMessageElasticServiceImpl extends ElasticServiceImpl<Long, ImMessageDoc> implements ImMessageElasticService {
    public ImMessageElasticServiceImpl(ElasticMapper<Long, ImMessageDoc> elasticMapper, ElasticsearchTemplate elasticsearchTemplate) {
        super(elasticMapper, elasticsearchTemplate);
    }

    @Override
    public Class<ImMessageDoc> getDocumentClass() {
        return ImMessageDoc.class;
    }

    @Override
    public PageResult<ImMessageDoc> queryPage(Long from, Long removeTime, MessagesRequestParamDTO params) {
        Long to = params.getToContactId();
        //构建查询条件.
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        Query fromQuery = getMustBooleanQuery(from, to, params, removeTime);
        Query toQuery = getMustBooleanQuery(to, from, params, removeTime);
        List<Query> shouldQueries = Arrays.asList(fromQuery, toQuery);
        queryBuilder.withQuery(q -> q.bool(b -> b.should(shouldQueries)));
        //order by `created` DESC
        queryBuilder.withSort(Sort.by(Sort.Direction.DESC, "created"));
        System.out.println(queryBuilder.build().getQuery());
        return pageQueryByBuilder(params.getPage(), params.getLimit(), queryBuilder);
    }

    @Override
    public List<ImMessageDoc> queryUnreadMessages(Long from, Long to) {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        Query fromQuery = Query.of(q -> q.term(t -> t.field("from").value(from)));
        Query toQuery = Query.of(q -> q.term(t -> t.field("to").value(to)));
        Query readQuery = Query.of(q -> q.term(t -> t.field("read").value(false)));
        queryBuilder.withQuery(q -> q.bool(b -> b.must(Arrays.asList(fromQuery, toQuery, readQuery))));
        queryBuilder.withMaxResults(10000);
        queryBuilder.withFields("id");
        return searchByQuery(queryBuilder.build());
    }

    private Query getMustBooleanQuery(Long from, Long to) {
        Query fromQuery = Query.of(q -> q.term(t -> t.field("from").value(from)));
        Query toQuery = Query.of(q -> q.term(t -> t.field("to").value(to)));
        return Query.of(q -> q.bool(b -> b.must(Arrays.asList(fromQuery, toQuery))));
    }

    private Query getMustBooleanQuery(Long from, Long to, MessagesRequestParamDTO params, Long removeTime) {
        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(Query.of(q -> q.term(t -> t.field("from").value(from))));
        mustQueries.add(Query.of(q -> q.term(t -> t.field("to").value(to))));
        if (removeTime != null) {
            mustQueries.add(QueryBuilders.range(m -> m.field("created").gt(JsonData.of(removeTime))));
        }
        if (params.getIsGroup() != null) {
            mustQueries.add(QueryBuilders.term(m -> m.field("group").value(params.getIsGroup())));
        }
        String type = params.getType();
        if (StringUtils.isNotBlank(type)) {
            mustQueries.add(QueryBuilders.term(m -> m.field("type").value(type)));
        }
        if (ImMessageType.TEXT.type.equals(type) && StringUtils.isNotBlank(params.getKeywords())) {
            mustQueries.add(QueryBuilders.matchPhrase(m -> m.field("content").query(params.getKeywords())));
        }
        return Query.of(q -> q.bool(b -> b.must(mustQueries)));
    }


}
