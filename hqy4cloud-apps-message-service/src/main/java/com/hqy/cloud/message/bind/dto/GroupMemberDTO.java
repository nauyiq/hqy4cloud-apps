package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

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
    @Size(max = 120)
    private String notice;
    private Integer role;
    @Size(max = 30)
    private String displayName;
    private Boolean deleted;

    public boolean isEnable() {
        return id != null && groupId != null;
    }
}
