package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddGroupMemberDTO {

    private Long id;
    private String username;
    private String avatar;
    private String nickname;
    private String intro;
    private Boolean inviteGroup;
    private Boolean status;
    private Long userId;
    private Boolean isGroupMember;







}
