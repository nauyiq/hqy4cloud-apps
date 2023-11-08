package com.hqy.cloud.apps.blog.vo;

import lombok.*;

import java.util.Date;

/**
 * AccountProfileVO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 17:35
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountProfileVO {

    private String id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private String intro;
    private Date birthday;

}
