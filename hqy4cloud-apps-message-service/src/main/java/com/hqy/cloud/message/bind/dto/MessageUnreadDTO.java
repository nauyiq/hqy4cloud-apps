package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/21 10:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageUnreadDTO {
    private Long conversationId;
    private Long userId;
    private Long toContactId;
    private Boolean isGroup;
    private Integer unread = 0;

    public boolean enabled() {
        if (isGroup == null) {
            return false;
        }
        return conversationId != null || toContactId != null;
    }



}
