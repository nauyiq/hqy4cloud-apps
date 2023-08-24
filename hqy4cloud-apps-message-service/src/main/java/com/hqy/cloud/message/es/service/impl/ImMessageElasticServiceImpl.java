package com.hqy.cloud.message.es.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.mapper.ElasticMapper;
import com.hqy.cloud.elasticsearch.service.impl.ElasticServiceImpl;
import com.hqy.cloud.message.bind.dto.MessagesRequestParamDTO;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.es.document.ImMessageDoc;
import com.hqy.cloud.message.es.service.ImMessageElasticService;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.BooleanQuery;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Service;

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
    public PageResult<ImMessageDoc> queryPage(Long from, MessagesRequestParamDTO params) {
        String keywords = params.getKeywords();
        String type = params.getType();
        Long to = params.getToContactId();
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        // query by from.
        Query fromQuery = getMustBooleanQuery(from, to);
        // query by to.
        Query toQuery = getMustBooleanQuery(to, from);
        queryBuilder.withQuery(q -> q.bool(b -> b.should(fromQuery, toQuery)));
        // query by term by `group`
        queryBuilder.withQuery(q -> q.term(t -> t.field("group").value(params.getIsGroup())));
        if (StringUtils.isNotBlank(type)) {
            //query term by `type`
            queryBuilder.withQuery(q -> q.term(t -> t.field("type").value(type)));
        }
        if (ImMessageType.TEXT.type.equals(type) && StringUtils.isNotBlank(keywords)) {
            //对内容进行分词搜索 并且只有是文本类型的时候进行搜索.
            queryBuilder.withQuery(q -> q.matchPhrase(m -> m.field("content").query(keywords)));
        }
        //order by `created` DESC
        queryBuilder.withSort(Sort.by(Sort.Direction.DESC, "created"));
        return pageQueryByBuilder(params.getPage(), params.getLimit(), queryBuilder);
    }

    private Query getMustBooleanQuery(Long from, Long to) {
        Query fromQuery = Query.of(q -> q.term(t -> t.field("from").value(from)));
        Query toQuery = Query.of(q -> q.term(t -> t.field("to").value(to)));
        return Query.of(q -> q.bool(b -> b.must(fromQuery, toQuery)));
    }
}
