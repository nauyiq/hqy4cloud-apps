package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @date 2023-08-12 11:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {

    private String id;
    private String username;
    private String displayName;
    private String nickname;
    private String avatar;
    private String remark;

    public UserInfoVO(String id) {
        this.id = id;
    }

    public UserInfoVO(String id, String displayName, String avatar) {
        this.id = id;
        this.displayName = displayName;
        this.avatar = avatar;
    }

    public UserInfoVO(String id, String username, String nickname, String avatar) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public static UserInfoVO of(Long id, String username, String nickname, String avatar){
        return of(id, username, nickname, avatar, null);
    }


    public static UserInfoVO of(Long id, String username, String nickname, String avatar, String remark) {
        return UserInfoVO.builder()
                .id(id.toString())
                .username(username)
                .nickname(nickname)
                .avatar(avatar)
                .remark(remark)
                .displayName(StringUtils.isBlank(remark) ? username : remark).build();
    }
}
