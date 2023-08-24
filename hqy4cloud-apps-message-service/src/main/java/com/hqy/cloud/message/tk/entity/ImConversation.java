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
    private String lastMessageType;
    private String lastMessageContent;
    private Date lastMessageTime;


    public ImConversation(Long id, Long contactId) {
        this.userId = id;
        this.contactId = contactId;
    }

    public ImConversation(Long userId, Long contactId, Boolean group) {
        this.userId = userId;
        this.contactId = contactId;
        this.group = group;
    }

    public ImConversation(Long id, Long contactId, Date date, boolean group) {
        super(date);
        this.userId = id;
        this.contactId = contactId;
        this.group = group;
    }

    public static ImConversation of(Long userId) {
       return new ImConversation(userId, null);
    }

    public static ImConversation of(Long contactId, boolean isGroup) {
        return of(null, contactId, isGroup);
    }

    public static ImConversation of(Long userId, Long contactId, boolean isGroup) {
        return new ImConversation(userId, contactId, isGroup);
    }

    public static ImConversation ofGroup(Long userId, Long groupId) {
        return new ImConversation(userId, groupId, new Date(), true);
    }

    public static List<ImConversation> ofGroup(Long groupId, Long id, List<Long> userIds) {
        Date now = new Date();
        List<ImConversation> contacts = new ArrayList<>(userIds.size() + 1);
        contacts.add(new ImConversation(id, groupId, now, true));
        userIds.forEach(userId -> contacts.add(new ImConversation(userId, groupId, now,true)));
        return contacts;
    }
}
