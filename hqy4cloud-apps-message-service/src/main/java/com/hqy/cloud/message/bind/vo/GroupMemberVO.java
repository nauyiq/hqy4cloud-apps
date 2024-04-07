package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 10:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberVO {

    private String userId;
    private Integer role;
    private String created;
    private UserInfoVO userInfo;

    public GroupMemberVO(String userId, Integer role, String created) {
        this.userId = userId;
        this.role = role;
        this.created = created;
    }
}
