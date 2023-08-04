package com.hqy.cloud.apps.blog.listener;

import com.hqy.cloud.apps.blog.entity.CanalArticleModel;
import com.hqy.cloud.apps.blog.converter.ArticleConverter;
import com.hqy.cloud.apps.blog.entity.Article;
import com.hqy.cloud.apps.blog.es.document.ArticleDoc;
import com.hqy.cloud.apps.blog.es.service.ArticleElasticService;
import com.hqy.cloud.apps.blog.service.tk.ArticleTkService;
import com.hqy.cloud.canal.core.processor.BaseCanalBinlogEventProcessor;
import com.hqy.cloud.canal.model.CanalBinLogResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 文章表 canal监听器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/18 17:51
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleCanalListener extends BaseCanalBinlogEventProcessor<CanalArticleModel> {
    private final ArticleTkService articleTkService;
    private final ArticleElasticService articleElasticService;

    @Override
    protected void processInsertInternal(CanalBinLogResult<CanalArticleModel> result) {
        Long id = result.getPrimaryKey();
        Article article = articleTkService.queryById(id);
        if (Objects.isNull(article)) {
            return;
        }
        ArticleDoc articleDoc = ArticleConverter.CONVERTER.convertDoc(article);
        try {
            articleElasticService.save(articleDoc);
        } catch (Throwable cause) {
            log.error("Failed execute to insert articleDoc to es, cause: {}.", cause.getMessage(), cause);
        }
    }

    @Override
    protected void processUpdateInternal(CanalBinLogResult<CanalArticleModel> result) {
        CanalArticleModel canalArticleModel = result.getAfterData();
        ArticleDoc articleDoc = ArticleConverter.CONVERTER.convertDoc(canalArticleModel);
        try {
            articleElasticService.save(articleDoc);
        } catch (Throwable cause) {
            log.error("Failed execute to update articleDoc to es, cause: {}.", cause.getMessage(), cause);
        }
    }

    @Override
    protected void processDeleteInternal(CanalBinLogResult<CanalArticleModel> result) {
        try {
            Long key = result.getPrimaryKey();
            articleElasticService.deleteById(key);
        } catch (Throwable cause) {
            log.error("Failed execute to delete articleDoc from es, cause: {}.", cause.getMessage(), cause);
        }

    }
}
