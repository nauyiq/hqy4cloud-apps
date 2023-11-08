package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/21 14:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendContactDTO {

    private Long id;
    private String remark;
    private String avatar;
    private Boolean isNotice;
    private Boolean isTop;
    private String index;
}
