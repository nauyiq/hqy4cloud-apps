package com.hqy.cloud.apps.blog.es.document;

import cn.easyes.annotation.IndexField;
import cn.easyes.annotation.IndexId;
import cn.easyes.annotation.IndexName;
import cn.easyes.annotation.rely.FieldStrategy;
import cn.easyes.annotation.rely.FieldType;
import cn.easyes.annotation.rely.IdType;
import com.hqy.cloud.elasticsearch.document.EsDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.hqy.cloud.apps.blog.es.EsConstants.ARTICLE_INDEX_NAME;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/4/3 9:40
 */
@Data
@IndexName(value = ARTICLE_INDEX_NAME, shardsNum = 2, replicasNum = 2)
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDoc implements EsDocument {

    @IndexId(type = IdType.CUSTOMIZE)
    private String id;
    @IndexField(strategy = FieldStrategy.NOT_EMPTY)
    private String title;
    @IndexField(strategy = FieldStrategy.NOT_EMPTY)
    private String intro;
    @IndexField(strategy = FieldStrategy.NOT_EMPTY)
    private String cover;
    @IndexField(fieldType = FieldType.TEXT)
    private String content;
    private Integer type;
    private String backgroundMusic;
    private String backgroundMusicName;
    private Long author;
    private Boolean status;
    private Boolean deleted;
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
    private Date created;
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
    private Date updated;


    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
