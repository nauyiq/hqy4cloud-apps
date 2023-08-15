package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 13:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_im_contact")
@EqualsAndHashCode(callSuper = true)
public class ImContact extends BaseEntity<Long> {

    private Long userId;
    private Long contactId;
    private Boolean group;
    private Boolean notice;
    private Boolean top;
    private String lastMessageType;
    private String lastMessageContent;
    private Date lastMessageTime;
    private Date created;
    private Date updated;

    public ImContact(Long contactId) {
        this.contactId = contactId;
    }

    public ImContact(Long id, Long contactId) {
        this.userId = id;
        this.contactId = contactId;
    }

    public ImContact(Long id, Long contactId, Boolean group) {
        this.userId = id;
        this.contactId = contactId;
        this.group = group;
        Date now = new Date();
        this.created = now;
        this.updated = now;
    }

    public static ImContact of(Long contactId, boolean isGroup) {
        ImContact contact = new ImContact(contactId);
        contact.setGroup(isGroup);
        return contact;
    }

    public static ImContact of(Long userId, Long contactId, boolean isGroup) {
        ImContact contact = new ImContact(userId, contactId);
        contact.setGroup(isGroup);
        return contact;
    }

    public static ImContact ofGroup(Long userId, Long groupId) {
        return new ImContact(userId, groupId, true);
    }

    public static List<ImContact> ofGroup(Long groupId, Long id, List<Long> userIds) {
        List<ImContact> contacts = new ArrayList<>(userIds.size() + 1);
        contacts.add(new ImContact(id, groupId, true));
        userIds.forEach(userId -> contacts.add(new ImContact(userId, groupId, true)));
        return contacts;
    }
}
