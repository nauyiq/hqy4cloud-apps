package com.hqy.cloud.message.bind.dto;

import jodd.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/10/12 17:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForwardMessageDTO {

    private Long messageId;
    private List<Forward> forwards;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Forward {
        private Long contactId;
        private Boolean group;
        private ImMessageDTO message;

        public boolean enable() {
            return contactId != null && group != null && message != null && StringUtil.isNotBlank(message.getId());
        }

    }


}
