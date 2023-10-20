package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 10:40
 */
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_im_user_setting")
public class ImUserSetting extends BaseEntity<Long> {

    @Column(name = "is_private_chat")
    private Boolean privateChat;
    @Column(name = "is_invite_group")
    private Boolean inviteGroup;
    @Column(name = "is_online")
    private Boolean oline;
    @Column(name = "is_global_chat")
    private Boolean globalChat;

    public ImUserSetting(Long userId) {
        setId(userId);
    }

    public Boolean getPrivateChat() {
        return privateChat;
    }

    public void setPrivateChat(Boolean privateChat) {
        this.privateChat = privateChat;
    }

    public Boolean getInviteGroup() {
        return inviteGroup;
    }

    public void setInviteGroup(Boolean inviteGroup) {
        this.inviteGroup = inviteGroup;
    }

    public Boolean getOline() {
        return oline;
    }

    public void setOline(Boolean oline) {
        this.oline = oline;
    }

    public Boolean getGlobalChat() {
        return globalChat;
    }

    public void setGlobalChat(Boolean globalChat) {
        this.globalChat = globalChat;
    }

    public static ImUserSetting of(Long userId) {
        ImUserSetting userSetting = new ImUserSetting(false, true, true, false);
        Date now = new Date();
        userSetting.setCreated(now);
        userSetting.setUpdated(now);
        userSetting.setId(userId);
        return userSetting;
    }

}
