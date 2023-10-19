package com.hqy.cloud.message.es.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.mapper.ElasticMapper;
import com.hqy.cloud.elasticsearch.service.impl.ElasticServiceImpl;
import com.hqy.cloud.message.bind.dto.MessagesRequestParamDTO;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.es.document.ImMessageDoc;
import com.hqy.cloud.message.es.service.ImMessageElasticService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/23 13:37
 */
@Service
public class ImMessageElasticServiceImpl extends ElasticServiceImpl<Long, ImMessageDoc> implements ImMessageElasticService {
    private final ElasticsearchTemplate elasticsearchTemplate;
    public ImMessageElasticServiceImpl(ElasticMapper<Long, ImMessageDoc> elasticMapper, ElasticsearchTemplate elasticsearchTemplate) {
        super(elasticMapper, elasticsearchTemplate);
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Class<ImMessageDoc> getDocumentClass() {
        return ImMessageDoc.class;
    }

    @Override
    public PageResult<ImMessageDoc> queryPage(Long from, Long removeTime, Long deleteTime, MessagesRequestParamDTO params) {
        NativeQueryBuilder queryBuilder = getImMessageNativeQueryBuilder(from, removeTime, deleteTime, params);
        return pageQueryByBuilder(params.getPage(), params.getLimit(), queryBuilder);
    }

    @NotNull
    private NativeQueryBuilder getImMessageNativeQueryBuilder(Long from, Long removeTime, Long deleteTime, MessagesRequestParamDTO params) {
        Long to = params.getToContactId();
        //构建查询条件.
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        if (params.getIsGroup() == null || !params.getIsGroup()) {
            Query fromQuery = getPrivateChatMustBooleanQuery(from, to, params, removeTime, deleteTime);
            Query toQuery = getPrivateChatMustBooleanQuery(to, from, params, removeTime, deleteTime);
            List<Query> shouldQueries = Arrays.asList(fromQuery, toQuery);
            queryBuilder.withQuery(q -> q.bool(b -> b.should(shouldQueries)));
        } else {
            List<Query> mustQueries = new ArrayList<>();
            mustQueries.add(Query.of(q -> q.term(t -> t.field("to").value(to))));
            String type = params.getType();
            if (StringUtils.isNotBlank(type)) {
                mustQueries.add(QueryBuilders.term(m -> m.field("type").value(type)));
            }
            if (ImMessageType.TEXT.type.equals(type) && StringUtils.isNotBlank(params.getKeywords())) {
                mustQueries.add(QueryBuilders.matchPhrase(m -> m.field("content").query(params.getKeywords())));
            }
            if (deleteTime != null) {
                mustQueries.add(QueryBuilders.range(m -> m.field("created").lte(JsonData.of(deleteTime))));
            }
            if (removeTime != null) {
                mustQueries.add(QueryBuilders.range(m -> m.field("created").gte(JsonData.of(removeTime))));
            }
            queryBuilder.withQuery(q -> q.bool(b -> b.must(mustQueries)));
        }
        //order by `id` DESC
        queryBuilder.withSort(Sort.by(Sort.Direction.DESC, "id"));
        return queryBuilder;
    }

    /**
     * 针对单机应用， 如果分布式部署一定要改为分布式缓存.. 这里是由于个人原因只会是单机部署
     */
    private static final Cache<String, List<Object>> CACHE_HITS = CacheBuilder.newBuilder().expireAfterAccess(5L, TimeUnit.MINUTES).build();

    @Override
    public PageResult<ImMessageDoc> queryPageSearchAfter(Long send, Long removeTime, Long deleteTime, MessagesRequestParamDTO params) {
        NativeQueryBuilder queryBuilder = getImMessageNativeQueryBuilder(send, removeTime, deleteTime, params);
        Integer limit = params.getLimit();
        Integer page = params.getPage();
        String key = params.gerSearchAfterKey(removeTime, deleteTime);
        List<Object> searchAfter = CACHE_HITS.getIfPresent(key);
        SearchHits<ImMessageDoc> searchHits;
        if (CollectionUtils.isEmpty(searchAfter)) {
            //设置分页参数
            queryBuilder.withPageable(PageRequest.of(page - 1, limit));
            searchHits = elasticsearchTemplate.search(queryBuilder.build(), getDocumentClass());
        } else {
            //设置分页参数
            queryBuilder.withPageable(PageRequest.of(0, limit));
            NativeQuery query = queryBuilder.build();
            query.setSearchAfter(searchAfter);
            searchHits = elasticsearchTemplate.search(query, getDocumentClass());
        }
        if (searchHits.getSearchHits().size() != 0) {
            searchAfter = searchHits.getSearchHit(searchHits.getSearchHits().size() - 1).getSortValues();
        }
        if (CollectionUtils.isNotEmpty(searchAfter)) {
            CACHE_HITS.put(key, searchAfter);
        }
        return buildPageResult(page, limit, searchHits);
    }

    @Override
    public List<ImMessageDoc> queryUnreadMessages(Long from, Long to) {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        Query fromQuery = Query.of(q -> q.term(t -> t.field("from").value(from)));
        Query toQuery = Query.of(q -> q.term(t -> t.field("to").value(to)));
        Query readQuery = Query.of(q -> q.term(t -> t.field("read").value(false)));
        queryBuilder.withQuery(q -> q.bool(b -> b.must(Arrays.asList(fromQuery, toQuery, readQuery))));
//        queryBuilder.withMaxResults(1000);
        queryBuilder.withFields("id");
        return searchByQuery(queryBuilder.build());
    }


    private Query getPrivateChatMustBooleanQuery(Long from, Long to, MessagesRequestParamDTO params, Long removeTime, Long deleteTime) {
        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(Query.of(q -> q.term(t -> t.field("from").value(from))));
        mustQueries.add(Query.of(q -> q.term(t -> t.field("to").value(to))));
        if (deleteTime != null) {
            // 可以看到删除会话之前的聊天记录
            mustQueries.add(QueryBuilders.range(m -> m.field("created").lte(JsonData.of(deleteTime))));
        }
        if (removeTime != null) {
            // 可以看到移除会话之后的聊天记录
            mustQueries.add(QueryBuilders.range(m -> m.field("created").gte(JsonData.of(removeTime))));
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
