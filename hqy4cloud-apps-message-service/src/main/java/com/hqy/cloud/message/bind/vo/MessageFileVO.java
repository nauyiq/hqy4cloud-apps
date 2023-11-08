package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @date 2023-08-12 11:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageFileVO {

    /**
     * 文件路径
     */
    private String host;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String type;


}
