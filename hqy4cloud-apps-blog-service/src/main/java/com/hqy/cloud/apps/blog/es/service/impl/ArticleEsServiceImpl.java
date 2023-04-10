package com.hqy.cloud.apps.blog.es.service.impl;

import com.hqy.cloud.apps.blog.es.document.ArticleDoc;
import com.hqy.cloud.apps.blog.es.mapper.ArticleEsMapper;
import com.hqy.cloud.apps.blog.es.service.ArticleEsService;
import com.hqy.cloud.elasticsearch.mapper.EsMapper;
import com.hqy.cloud.elasticsearch.service.impl.EsServiceImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/4/3 10:07
 */
@Service
public class ArticleEsServiceImpl extends EsServiceImpl<ArticleDoc> implements ArticleEsService {
    private final ArticleEsMapper articleEsMapper;

    public ArticleEsServiceImpl(RestHighLevelClient client, ArticleEsMapper articleEsMapper) {
        super(client);
        this.articleEsMapper = articleEsMapper;
    }

    @Override
    public Class<ArticleDoc> getDocumentClass() {
        return ArticleDoc.class;
    }

    @Override
    public EsMapper<ArticleDoc> getMapper() {
        return articleEsMapper;
    }
}
