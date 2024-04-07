package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImUserSettingInfoDTO {

    private Long id;

    @NotEmpty
    @Size(min = 2, max = 16)
    private String nickname;

    @Size(max = 120)
    private String intro;

    private String avatar;


}
