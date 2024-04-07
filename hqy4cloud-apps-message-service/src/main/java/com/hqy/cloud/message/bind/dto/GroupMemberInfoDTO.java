package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberInfoDTO {

    private Long userId;
    private String displayName;
    private String avatar;
    private String nickname;
    private Integer role;
    private Date created;


}
