package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/31 16:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImChatConfigDTO {

    private Long contactId;
    private Boolean status;
    private Boolean isGroup;

    public boolean isEnabled() {
        return contactId != null && status != null && isGroup != null;
    }
}
