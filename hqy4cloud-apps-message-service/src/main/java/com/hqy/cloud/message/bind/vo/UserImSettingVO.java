package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * @author qiyuan.hong
 * @date 2023-08-12 11:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserImSettingVO {

    /**
     * 用户id
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 聊天昵称
     */
    @Size(max = 30)
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 简介
     */
    private String intro;


    /**
     * 是否允许邀请加入群聊
     */
    private Boolean isInviteGroup;


    /**
     * 是否允许同步设置到账号设置
     */
    private Boolean isSyncSetting;


    /**
     * 是否可以通过账号名搜索到你
     */
    private Boolean isQueryAccount;



}
