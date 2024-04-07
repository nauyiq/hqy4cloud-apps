package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendApplicationDTO {

    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private Integer status;
    private String remark;
    private Date created;



}



