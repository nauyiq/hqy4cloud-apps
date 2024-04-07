package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImMessageEventContentDTO {

    private Long operatorId;
    private String operatorName;

}
