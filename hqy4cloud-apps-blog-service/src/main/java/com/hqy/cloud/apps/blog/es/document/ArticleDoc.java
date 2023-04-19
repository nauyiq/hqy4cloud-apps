package com.hqy.cloud.apps.blog.es.document;

import com.hqy.cloud.apps.blog.es.EsConstants;
import com.hqy.cloud.elasticsearch.document.ElasticDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/4/3 9:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = EsConstants.ARTICLE_INDEX_NAME)
public class ArticleDoc implements ElasticDocument<Long> {

    @Id
    private Long id;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String intro;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String cover;

    @Field(type = FieldType.Keyword)
    private String content;

    @Field(type = FieldType.Integer)
    private Integer type;

    @Field(type = FieldType.Keyword)
    private String backgroundMusic;

    @Field(type = FieldType.Keyword)
    private String backgroundMusicName;

    @Field(type = FieldType.Long)
    private Long author;

    @Field(type = FieldType.Boolean)
    private Boolean status;

    @Field(type = FieldType.Boolean)
    private Boolean deleted;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
    private Date created;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
    private Date updated;

}
