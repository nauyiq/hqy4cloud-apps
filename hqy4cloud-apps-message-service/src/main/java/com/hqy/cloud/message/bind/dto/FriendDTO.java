package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 11:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendDTO {

    /**
     * 添加的好友用户id
     */
    private Long userId;

    /**
     * 备注
     */
    private String remark;

    /**
     * accept or reject
     */
    private Boolean status;



}
