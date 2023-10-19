package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 聊天会话表 entity.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 13:19
 */
@Data
@Builder
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
    private Boolean notice;
    @Column(name = "is_top")
    private Boolean top;
    private Long lastRemoveTime;
    private String lastMessageType;
    private String lastMessageContent;
    private Date lastMessageTime;
    private Long deleted;

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
        this.lastMessageTime = date;
        this.group = group;
        this.notice = true;
        this.top = false;
    }

    public ImConversation(Long id, Long contactId, Date date, boolean group, String lastMessageContent, String lastMessageType) {
        super(date);
        this.userId = id;
        this.contactId = contactId;
        this.lastMessageTime = date;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageType = lastMessageType;
        this.lastRemoveTime = null;
        this.group = group;
        this.notice = true;
        this.top = false;
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

    public static List<ImConversation> ofFriend(Long from, Long to, String fromNickname, String toNickname) {
        Date now = new Date();
        ImConversation fromConversation = ofFriend(from, to, toNickname, now);
        ImConversation toConversation = ofFriend(to, from, fromNickname, now);
        return Arrays.asList(fromConversation, toConversation);
    }

    public static ImConversation ofDefault(Long from, Long to, Boolean isGroup) {
        ImConversation conversation = new ImConversation(from, to, isGroup);
        Date now = new Date();
        conversation.setTop(false);
        conversation.setNotice(true);
        conversation.setCreated(now);
        conversation.setUpdated(now);
        return conversation;
    }

    public static ImConversation ofFriend(Long from, Long to, String nickname, Date now) {
        ImConversation imConversation = new ImConversation(from, to, now, false);
        imConversation.setNotice(true);
        imConversation.setTop(false);
        imConversation.setLastMessageType(ImMessageType.SYSTEM.type);
        imConversation.setLastMessageTime(now);
        imConversation.setLastMessageContent(nickname);
        return imConversation;
    }

    public static List<ImConversation> ofGroup(Long groupId, List<Long> userIds) {
        Date now = new Date();
        List<ImConversation> contacts = new ArrayList<>(userIds.size());
        userIds.forEach(userId -> contacts.add(new ImConversation(userId, groupId, now,true)));
        return contacts;
    }

}
