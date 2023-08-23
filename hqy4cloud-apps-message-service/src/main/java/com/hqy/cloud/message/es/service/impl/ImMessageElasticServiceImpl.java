package com.hqy.cloud.message.es.service.impl;

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
    public PageResult<ImMessageDoc> queryPage(MessagesRequestParamDTO params) {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        String keywords = params.getKeywords();
        String type = params.getType();
        Long conversationId = params.getConversationId();
        //term匹配会话id
        queryBuilder.withQuery(q -> q.term(t -> t.field("conversationId").value(conversationId)));
        if (StringUtils.isNotBlank(type)) {
            //term匹配t类型
            queryBuilder.withQuery(q -> q.term(t -> t.field("type").value(type)));
        }
        if (ImMessageType.TEXT.type.equals(type)) {
            if (StringUtils.isNotBlank(keywords)) {
                //对内容进行分词搜索 并且只有是文本类型的时候进行搜索.
                queryBuilder.withQuery(q -> q.matchPhrase(m -> m.field("content").query(keywords)));
            }
        }
        //根据时间升序排序
        queryBuilder.withSort(Sort.by(Sort.Direction.ASC, "created"));
        return pageQueryByBuilder(params.getPage(), params.getLimit(), queryBuilder);
    }
}
