package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {

    private Long id;
    private Long userId;
    private Long contactId;
    private String avatar;
    private String displayName;
    private String index;
    private Long creator;
    private Boolean isTop;
    private Boolean isNotice;
    private Boolean isGroup;

}
