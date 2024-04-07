package com.hqy.cloud.message.es.document;

import com.hqy.cloud.elasticsearch.document.ElasticDocument;
import com.hqy.cloud.message.bind.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 私聊消息 + 群聊消息, 宽表用于保存所有消息，主要用于聊天记录查询的逻辑
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = Constants.IM_MESSAGE_INDEX)
public class ImMessage implements ElasticDocument<String> {

    /**
     * ES文档主键
     */
    @Id
    private String id;

    /**
     * 消息id, 入库到数据库的主键id
     */
    @Field(type = FieldType.Long)
    private Long dbId;

    /**
     * 消息id 非唯一值
     */
    @Field(type = FieldType.Keyword)
    private String messageId;

    /**
     * 发送方id
     */
    @Field(type = FieldType.Long)
    private Long send;

    /**
     * 接收方id， 如果是群消息 则存储的是群聊id
     */
    @Field(type = FieldType.Long)
    private Long receive;

    /**
     * 是否是群聊消息
     */
    @Field(type = FieldType.Boolean)
    private Boolean isGroup;

    /**
     * 消息类型
     */
    @Field(type = FieldType.Integer)
    private Integer type;

    /**
     * 消息内容， 基于ik分词，用于聊天记录的模糊查询
     */
    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String content;

    /**
     * 如果是文件或者图片消息，存储的是文件路径。 文件路径不分词
     */
    @Field(type = FieldType.Keyword)
    private String path;

    /**
     * 如果是文件类型的消息，存储的是源文件名
     */
    @Field(type = FieldType.Keyword)
    private String fileName;

    /**
     * 如果是文件类型的消息，存储的是源文件大小
     */
    @Field(type = FieldType.Long)
    private Long fileSize;

    /**
     * 存储消息的状态
     */
    @Field(type = FieldType.Integer)
    private Integer status;

    /**
     * 消息创建的时间戳
     */
    @Field(type = FieldType.Long)
    private Long created;





}
