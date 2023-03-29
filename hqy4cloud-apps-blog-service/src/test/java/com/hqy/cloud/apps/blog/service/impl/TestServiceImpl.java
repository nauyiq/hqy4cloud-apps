package com.hqy.cloud.apps.blog.service.impl;

import com.hqy.cloud.apps.blog.document.TestDocument;
import com.hqy.cloud.apps.blog.service.TestService;
import com.hqy.cloud.elasticsearch.mapper.EsMapper;
import com.hqy.cloud.elasticsearch.service.impl.EsServiceImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/29 10:17
 */
@Service
public class TestServiceImpl extends EsServiceImpl<TestDocument> implements TestService {

    public TestServiceImpl(EsMapper<TestDocument> mapper, RestHighLevelClient client) {
        super(TestDocument.class, mapper, client);
    }


}
