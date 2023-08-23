package com.hqy.cloud.message.bind.vo;

import com.hqy.cloud.message.bind.dto.FriendContactDTO;
import com.hqy.cloud.message.bind.dto.GroupContactDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/21 13:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactVO {

    /**
     * 好友id
     */
    private String id;

    /**
     * 名称
     */
    private String displayName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否是群
     */
    private Boolean isGroup;

    /**
     * 是否消息提醒
     */
    private Boolean isNotice;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 是否接收群邀请
     */
    private Boolean isInvite;

    /**
     * 排序index
     */
    private String index;

    public ContactVO(FriendContactDTO contact) {
        this.id = contact.getId().toString();
        this.displayName = contact.getRemark();
        this.avatar = contact.getAvatar();
        this.isGroup = false;
        this.isNotice = contact.getIsNotice();
        this.isTop = contact.getIsTop();
        this.isInvite = false;
        this.index = contact.getIndex();
    }

    public ContactVO(GroupContactDTO contact) {
        this.id = contact.getGroupId().toString();
        this.displayName = contact.getName();
        this.avatar = contact.getGroupAvatar();
        this.isGroup = true;
        this.isNotice = contact.getGroupNotice();
        this.isTop = contact.getGroupTop();
        this.isInvite = contact.getGroupInvite();
        this.index = contact.getGroupIndex();
    }
}
