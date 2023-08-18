package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 聊天会话表 entity.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 13:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_im_conversation")
@EqualsAndHashCode(callSuper = true)
public class ImConversation extends BaseEntity<Long> {

    private Long userId;
    private Long contactId;
    @Column(name = "is_group")
    private Boolean group;
    @Column(name = "is_notice")
    private Boolean notice = true;
    @Column(name = "is_top")
    private Boolean top = false;
    private String displayName;
    private String lastMessageType;
    private String lastMessageContent;
    private Date lastMessageTime;
    private Date created;
    private Date updated;


    public ImConversation(Long id, Long contactId) {
        this.userId = id;
        this.contactId = contactId;
    }

    public ImConversation(Long userId, Long contactId, Boolean group) {
        this.userId = userId;
        this.contactId = contactId;
        this.group = group;
    }

    public ImConversation(Long id, Long contactId, String groupName) {
        this.userId = id;
        this.contactId = contactId;
        this.group = true;
        this.displayName = groupName;
        Date now = new Date();
        this.created = now;
        this.updated = now;
    }

    public static ImConversation of(Long userId) {
       return new ImConversation(userId, null);
    }

    public static ImConversation of(Long contactId, boolean isGroup) {
        return of(null, contactId, isGroup);
    }

    public static ImConversation ofGroup(Long userId, Long groupId) {
        return of(userId, groupId, true);
    }

    public static ImConversation of(Long userId, Long contactId, boolean isGroup) {
        return new ImConversation(userId, contactId, isGroup);
    }

    public static List<ImConversation> ofGroup(Long groupId, Long id, String groupName, List<Long> userIds) {
        List<ImConversation> contacts = new ArrayList<>(userIds.size() + 1);
        contacts.add(new ImConversation(id, groupId, groupName));
        userIds.forEach(userId -> contacts.add(new ImConversation(userId, groupId, groupName)));
        return contacts;
    }
}
