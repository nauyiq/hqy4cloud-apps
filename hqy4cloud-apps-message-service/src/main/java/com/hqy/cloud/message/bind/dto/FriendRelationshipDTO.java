package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRelationshipDTO {

    private Long apply;
    private Long receive;
    private List<Long> id;

}
