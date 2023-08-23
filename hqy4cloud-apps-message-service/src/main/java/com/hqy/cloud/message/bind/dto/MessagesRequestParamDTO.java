package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/23 11:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagesRequestParamDTO {

    private Integer page = 1;
    private Integer limit = 20;
    private String type;
    private String keywords;
    private Long conversationId;

}
