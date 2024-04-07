package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImUserInfoDTO {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String remark;


}
