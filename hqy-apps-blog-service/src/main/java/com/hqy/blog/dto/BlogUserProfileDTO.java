package com.hqy.blog.dto;

import lombok.Data;

/**
 * BlogUserProfileDTO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 18:02
 */
@Data
public class BlogUserProfileDTO {

    private Long id;
    private String avatar;
    private String nickname;
    private String birthday;
    private String intro;

    public BlogUserProfileDTO() {
    }

    public BlogUserProfileDTO(Long id, String avatar, String nickname, String birthday, String intro) {
        this.id = id;
        this.avatar = avatar;
        this.nickname = nickname;
        this.birthday = birthday;
        this.intro = intro;
    }
}
