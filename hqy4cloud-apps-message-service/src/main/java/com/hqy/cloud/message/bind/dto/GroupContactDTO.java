package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/21 14:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupContactDTO {

    private Long groupId;
    private String name;
    private String groupIndex;
    private String groupAvatar;
    private Long creator;
    private Boolean groupNotice;
    private Boolean groupInvite;
    private Boolean groupTop;





}
