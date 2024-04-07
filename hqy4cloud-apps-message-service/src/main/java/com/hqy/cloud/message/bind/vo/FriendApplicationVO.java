package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendApplicationVO {

    private Long id;
    private UserInfoVO info;
    private Integer status;
    private String applyRemark;
    private String created;

}
