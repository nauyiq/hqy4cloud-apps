package com.hqy.cloud.message.es.document;

import com.hqy.cloud.apps.commom.constants.AppsConstants;
import com.hqy.cloud.elasticsearch.document.ElasticDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/23 13:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = AppsConstants.Message.IM_MESSAGE_INDEX)
public class ImMessageDoc implements ElasticDocument<Long> {

    @Id
    private Long id;
    @Field(type = FieldType.Keyword)
    private String messageId;
    @Field(type = FieldType.Long)
    private Long conversationId;
    @Field(type = FieldType.Long)
    private Long from;
    @Field(type = FieldType.Long)
    private Long to;
    @Field(type = FieldType.Boolean)
    private Boolean group;
    @Field(type = FieldType.Keyword)
    private String type;
    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String content;
    @Field(type = FieldType.Keyword)
    private String path;
    @Field(type = FieldType.Boolean)
    private Boolean read;
    @Field(type = FieldType.Boolean)
    private Boolean status;
    @Field(type = FieldType.Long)
    private Long created;

}
