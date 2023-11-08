package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {

    /**
     * 群id
     */
    private Long groupId;

    /**
     * 群名
     */
    private String name;

    /**
     * 群公告
     */
    private String notice;

    /**
     * 群成员id集合
     */
    private List<Long> userIds;






}
