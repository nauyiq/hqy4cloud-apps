package com.hqy.cloud.apps.blog.es.service;

import com.hqy.cloud.apps.blog.es.document.ArticleDoc;
import com.hqy.cloud.elasticsearch.service.EsService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/4/3 10:06
 */
public interface ArticleEsService extends EsService<ArticleDoc> {
}
