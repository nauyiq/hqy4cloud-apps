package com.hqy.cloud.message.bind.vo;

import com.hqy.cloud.foundation.common.account.AccountAvatarUtil;
import com.hqy.cloud.message.bind.ConvertUtil;
import com.hqy.cloud.message.bind.dto.ContactDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/21
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
     * 群聊创建者
     */
    private String creator;

    /**
     * 排序index
     */
    private String index;


    public static ContactVO of(ContactDTO contact) {
        return ContactVO.builder()
                .id(contact.getContactId().toString())
                .displayName(contact.getDisplayName())
                .avatar(AccountAvatarUtil.getAvatar(contact.getAvatar()))
                .isGroup(contact.getIsGroup())
                .isTop(contact.getIsTop())
                .creator(contact.getCreator() == null ? null : contact.getCreator().toString())
                .isNotice(contact.getIsNotice())
                .index(ConvertUtil.getIndex(contact.getIsGroup(), contact.getDisplayName())).build();
    }

}
