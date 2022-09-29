package com.hqy.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * AccountProfileVO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 17:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountProfileVO {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String intro;
    private Date birthday;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("username", username)
                .append("nickname", nickname)
                .append("avatar", avatar)
                .append("intro", intro)
                .append("birthday", birthday)
                .toString();
    }
}
