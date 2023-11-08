package com.hqy.cloud.message.es.document;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.apps.commom.constants.AppsConstants;
import com.hqy.cloud.elasticsearch.document.ElasticDocument;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.tk.entity.ImMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    private Long from;
    @Field(type = FieldType.Long)
    private Long to;
    @Field(type = FieldType.Boolean)
    private Boolean group;
    @Field(type = FieldType.Boolean)
    private Boolean read;
    @Field(type = FieldType.Keyword)
    private String type;
    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String content;
    @Field(type = FieldType.Keyword)
    private String path;
    @Field(type = FieldType.Keyword)
    private String fileName;
    @Field(type = FieldType.Long)
    private Long fileSize;
    @Field(type = FieldType.Boolean)
    private Boolean status;
    @Field(type = FieldType.Long)
    private Long created;

    public ImMessageDoc(ImMessage message) {
        this.id = message.getId();
        this.messageId = message.getMessageId();
        this.from = message.getFrom();
        this.to = message.getTo();
        this.type = message.getType();
        this.group = message.getGroup();
        this.read = message.getRead();
        this.content = isTextMessage() ? message.getContent() : StrUtil.EMPTY;
        this.path = StringUtils.isBlank(this.content) ? message.getContent() : StrUtil.EMPTY;
        this.fileName = message.getFileName();
        this.fileSize = message.getFileSize();
        this.status = message.getStatus();
        this.created = message.getCreated().getTime();
    }

    public boolean isTextMessage() {
        return this.type.equals(ImMessageType.TEXT.type) ||
                this.type.equals(ImMessageType.SYSTEM.type) || this.type.equals(ImMessageType.EVENT.type);
    }

}
