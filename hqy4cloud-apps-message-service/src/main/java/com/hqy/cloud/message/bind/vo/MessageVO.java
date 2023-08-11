package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 16:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {

    /**
     * 消息id
     */
    private String id;

    /**
     * 是否是群聊消息
     */
    private Boolean isGroup;






}
