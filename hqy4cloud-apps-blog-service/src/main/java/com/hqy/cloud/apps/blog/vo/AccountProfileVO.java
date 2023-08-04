package com.hqy.cloud.apps.blog.vo;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.struct.ChatgptConfigStruct;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * AccountProfileVO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 17:35
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountProfileVO {

    private String id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private String intro;
    private Date birthday;
    private ChatgptConfigStruct chatgptConfig;

    public AccountProfileVO(AccountInfoDTO accountInfo) {
        AssertUtil.notNull(accountInfo, "Account Info should not be null.");
        this.id = accountInfo.getId().toString();
        this.username = accountInfo.getUsername();
        this.nickname = accountInfo.getNickname();
        this.email = accountInfo.getEmail();
        this.phone = accountInfo.getPhone();
        this.avatar = accountInfo.getAvatar();
        this.birthday = accountInfo.getBirthday();
        String chatgptConfig = accountInfo.getChatgptConfig();
        this.chatgptConfig = StringUtils.isBlank(chatgptConfig) ? new ChatgptConfigStruct() : JsonUtil.toBean(chatgptConfig, ChatgptConfigStruct.class);
    }
}
