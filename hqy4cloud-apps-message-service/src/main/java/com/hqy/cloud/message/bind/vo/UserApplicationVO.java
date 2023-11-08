package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/6 16:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserApplicationVO {

    private Long id;
    private String receive;
    private String send;
    private String remark;
    private UserInfoVO info;
    private Integer status;
    private String created;



}
