package com.hqy.cloud.apps.blog.document;

import cn.easyes.annotation.IndexId;
import cn.easyes.annotation.IndexName;
import cn.easyes.annotation.rely.IdType;
import com.hqy.cloud.elasticsearch.document.EsDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/29 10:10
 */
@Data
@IndexName("test")
//@Document(indexName = "test")
//@AllArgsConstructor
//@NoArgsConstructor
public class TestDocument implements EsDocument {

    @IndexId(type = IdType.CUSTOMIZE)
    private String id;
    private String name;
    private String description;






}
