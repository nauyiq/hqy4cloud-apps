package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件类型消息DTO
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMessageDTO {

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件长度
     */
    private Long fileSize;

    /**
     * 原始文件名
     */
    private String fileName;



}
