package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @date 2023-08-12 11:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {

    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private String mark;


}
