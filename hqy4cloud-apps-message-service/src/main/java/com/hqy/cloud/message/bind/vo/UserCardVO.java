package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/31 9:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCardVO {

    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private String intro;
    private FriendVO friend;

    public UserCardVO(String id, String username, String nickname, String avatar, String intro) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.intro = intro;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendVO {
        private Boolean isTop;
        private Boolean isNotice;
        private String remark;
    }


}
