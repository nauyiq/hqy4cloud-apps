package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 10:40
 */
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_im_user_setting")
public class ImUserSetting extends BaseEntity<Long> {

    private Boolean privateChat;
    private Boolean inviteGroup;
    private Boolean oline;
    private Boolean clearMsg;
    private Integer clearMsgDate;

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

    public Boolean getClearMsg() {
        return clearMsg;
    }

    public void setClearMsg(Boolean clearMsg) {
        this.clearMsg = clearMsg;
    }

    public Integer getClearMsgDate() {
        return clearMsgDate;
    }

    public void setClearMsgDate(Integer clearMsgDate) {
        this.clearMsgDate = clearMsgDate;
    }
}