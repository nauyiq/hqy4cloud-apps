package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 9:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberDTO {

    private Long id;
    private Long groupId;
    private String groupName;
    private String groupAvatar;
    private Boolean groupInvite;
    private Long groupCreator;
    private String notice;
    private Integer role;
    private String displayName;

    public boolean isEnable() {
        return id != null && groupId != null;
    }
}
