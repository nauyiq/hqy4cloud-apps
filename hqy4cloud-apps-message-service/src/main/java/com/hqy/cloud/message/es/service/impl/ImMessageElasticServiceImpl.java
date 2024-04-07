package com.hqy.cloud.message.es.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.mapper.ElasticMapper;
import com.hqy.cloud.elasticsearch.service.impl.ElasticServiceImpl;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.db.entity.GroupConversation;
import com.hqy.cloud.message.es.document.ImMessage;
import com.hqy.cloud.message.es.service.ImMessageElasticService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
@Service
public class ImMessageElasticServiceImpl extends ElasticServiceImpl<String, ImMessage> implements ImMessageElasticService {

    public ImMessageElasticServiceImpl(ElasticMapper<String, ImMessage> elasticMapper, ElasticsearchTemplate elasticsearchTemplate) {
        super(elasticMapper, elasticsearchTemplate);
    }

    @Override
    public Class<ImMessage> getDocumentClass() {
        return ImMessage.class;
    }

    @Override
    public PageResult<ImMessage> searchPageMessages(Integer page, Integer limit, Long accountId, Long contactId, Long lastRemoveTime, MessageType messageType, String keywords, Boolean group) {
        //构建查询条件.
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        if (contactId != null && group != null) {
            if (group) {
                // 查找群聊聊天会话聊天记录
                List<Query> mustQueries = new ArrayList<>();
                // 接收消息为群聊id
                mustQueries.add(Query.of(q -> q.term(t -> t.field("receive").value(contactId))));
                mustQueries.add(Query.of(q -> q.term(t -> t.field("isGroup").value(true))));
                if (messageType != null) {
                    // 根据类型查找
                    mustQueries.add(QueryBuilders.term(m -> m.field("type").value(messageType.type)));
                }
                if (StringUtils.isNotBlank(keywords) && (messageType == null || messageType == MessageType.TEXT)) {
                    // 只有纯文本的消息类型 才进行关键词查找
                    mustQueries.add(QueryBuilders.matchPhrase(m -> m.field("content").query(keywords)));
                }
                if (lastRemoveTime != null) {
                    mustQueries.add(QueryBuilders.range(m -> m.field("created").gte(JsonData.of(lastRemoveTime))));
                }
                queryBuilder.withQuery(q -> q.bool(b -> b.must(mustQueries)));
            } else {
                // 查找私聊群聊会话聊天记录
                List<Query> shouldQueries = List.of(getPrivateChatQuery(accountId, contactId, lastRemoveTime, messageType, keywords), getPrivateChatQuery(contactId, accountId, lastRemoveTime, messageType, keywords));
                queryBuilder.withQuery(q -> q.bool(b -> b.should(shouldQueries)));
            }
        } else {
            // 业务上不会走到这个逻辑， 暂时不放开，因为相当于查询整个索引库的聊天记录
            if (messageType == null && StringUtils.isBlank(keywords)) {
                // 没有查询的条件, 直接返回空 或者根据分页查询所有， 这里返回空
                return new PageResult<>();
            }
            List<Query> mustQueries = new ArrayList<>();
            if (messageType != null) {
                // 根据类型查找
                mustQueries.add(QueryBuilders.term(m -> m.field("type").value(messageType.type)));
            }
            if (StringUtils.isNotBlank(keywords) && (messageType == null || messageType == MessageType.TEXT)) {
                // 只有纯文本的消息类型 才进行关键词查找
                mustQueries.add(QueryBuilders.matchPhrase(m -> m.field("content").query(keywords)));
            }
            queryBuilder.withQuery(q -> q.bool(b -> b.must(mustQueries)));
        }
        //order by `id` DESC
        queryBuilder.withSort(Sort.by(Sort.Direction.DESC, "dbId"));
        return pageQueryByBuilder(page, limit, queryBuilder);
    }


    @Override
    public PageResult<ImMessage> searchRemovedGroupMemberMessages(Integer page, Integer limit, GroupConversation groupConversation, MessageType messageType, String keywords) {
        Long lastRemoveTime = groupConversation.getLastRemoveTime();
        Date updated = groupConversation.getUpdated();
        //构建查询条件.
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        // 查找群聊聊天会话聊天记录
        List<Query> mustQueries = new ArrayList<>();
        // 接收消息为群聊id
        mustQueries.add(Query.of(q -> q.term(t -> t.field("receive").value(groupConversation.getGroupId()))));
        mustQueries.add(Query.of(q -> q.term(t -> t.field("isGroup").value(true))));
        if (messageType != null) {
            // 根据类型查找
            mustQueries.add(QueryBuilders.term(m -> m.field("type").value(messageType.type)));
        }
        if (StringUtils.isNotBlank(keywords) && (messageType == null || messageType == MessageType.TEXT)) {
            // 只有纯文本的消息类型 才进行关键词查找
            mustQueries.add(QueryBuilders.matchPhrase(m -> m.field("content").query(keywords)));
        }
        if (updated != null) {
            mustQueries.add(QueryBuilders.range(m -> m.field("created").lte(JsonData.of(updated.getTime()))));
        }
        if (lastRemoveTime != null) {
            mustQueries.add(QueryBuilders.range(m -> m.field("created").gte(JsonData.of(lastRemoveTime))));
        }
        queryBuilder.withQuery(q -> q.bool(b -> b.must(mustQueries)));
        queryBuilder.withSort(Sort.by(Sort.Direction.DESC, "dbId"));
        return pageQueryByBuilder(page, limit, queryBuilder);
    }

    private Query getPrivateChatQuery(Long send, Long receive, Long lastRemoveTime, MessageType messageType, String keywords) {
        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(Query.of(q -> q.term(t -> t.field("send").value(send))));
        mustQueries.add(Query.of(q -> q.term(t -> t.field("receive").value(receive))));
        mustQueries.add(Query.of(q -> q.term(t -> t.field("isGroup").value(false))));
        if (messageType != null) {
            // 根据类型查找
            mustQueries.add(QueryBuilders.term(m -> m.field("type").value(messageType.type)));
        }
        if (lastRemoveTime != null) {
            mustQueries.add(QueryBuilders.range(m -> m.field("created").gte(JsonData.of(lastRemoveTime))));
        }
        if (StringUtils.isNotBlank(keywords) && (messageType == null || messageType == MessageType.TEXT)) {
            // 只有纯文本的消息类型 才进行关键词查找
            mustQueries.add(QueryBuilders.matchPhrase(m -> m.field("content").query(keywords)));
        }
        return Query.of(q -> q.bool(b -> b.must(mustQueries)));
    }
}
