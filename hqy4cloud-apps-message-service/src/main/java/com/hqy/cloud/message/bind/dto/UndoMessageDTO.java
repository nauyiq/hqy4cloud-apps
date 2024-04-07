package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UndoMessageDTO {

    private Long id;
    private Boolean isGroup;

}
