package com.hqy.cloud.message.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * im用户设置表
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_im_user_setting")
public class UserSetting extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前用户id
     */
    private Long id;

    /**
     * 冗余账号RPC用户名
     */
    private String username;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 当前系统聊天昵称
     */
    private String nickname;

    /**
     * 简介
     */
    private String intro;


    /**
     * 是否允许邀请进入群聊
     */
    @TableField(value = "is_invite_group")
    private Boolean inviteGroup = true;

    /**
     * 是否允许同步设置到账号设置
     */
    @TableField(value = "is_sync_setting")
    private Boolean syncSetting = true;


    /**
     * 是否可以通过账号名搜索到你
     */
    @TableField(value = "is_query_account")
    private Boolean queryAccount = true;


    /**
     * 是否可用
     */
    private Boolean status;

    public UserSetting(String username, String avatar, String nickname, String intro) {
        super(new Date());
        this.username = username;
        this.avatar = avatar;
        this.nickname = nickname;
        this.intro = intro;
    }
}
