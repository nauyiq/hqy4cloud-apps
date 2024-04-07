package com.hqy.cloud.message.bind.dto;

import cn.hutool.core.util.StrUtil;
import jodd.util.StringUtil;
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

    private Long toContactId;
    private Long conversationId;
    private Boolean isGroup;

    public String gerSearchAfterKey(Long removeTime, Long deleteTime) {
        StringBuilder sb = new StringBuilder();
        if (StringUtil.isNotBlank(type)) {
            sb.append(type);
        }
        if (StringUtil.isNotBlank(keywords)) {
            sb.append(StrUtil.COLON).append(keywords);
        }
        if (toContactId != null) {
            sb.append(StrUtil.COLON).append(toContactId);
        }
        if (isGroup != null) {
            sb.append(StrUtil.COLON).append(isGroup);
        }
        if (removeTime != null) {
            sb.append(StrUtil.COLON).append(removeTime);
        }
        if (deleteTime != null) {
            sb.append(StrUtil.COLON).append(deleteTime);
        }
        return sb.toString();
    }



}
