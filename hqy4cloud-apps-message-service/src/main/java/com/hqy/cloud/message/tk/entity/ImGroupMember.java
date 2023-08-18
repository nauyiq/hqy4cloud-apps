package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.PrimaryLessBaseEntity;
import com.hqy.cloud.message.bind.enums.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 群聊用户表 entity
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_im_group_member")
public class ImGroupMember implements PrimaryLessBaseEntity {
    public static final int MAX_MEMBERS = 500;

    @Id
    private Long groupId;
    @Id
    private Long userId;
    private String displayName;
    private Integer role;
    @Column(name = "is_top")
    private Boolean top = false;
    @Column(name = "is_notice")
    private Boolean notice = true;
    private Date created;
    private Date updated;

    public ImGroupMember(Long groupId) {
        this.groupId = groupId;
    }

    public ImGroupMember(Long groupId, Long userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public ImGroupMember(Long groupId, Long userId, Integer role) {
        this.groupId = groupId;
        this.userId = userId;
        this.role = role;
        Date now = new Date();
        this.created = now;
        this.updated = now;
    }

    public static ImGroupMember of(Long groupId) {
        return new ImGroupMember(groupId);
    }

    public static ImGroupMember of(Long groupId, Long userId, String displayName, Integer role) {
        ImGroupMember member = new ImGroupMember(groupId, userId);
        member.setDisplayName(displayName);
        member.setRole(role);
        return member;
    }

    public static List<ImGroupMember> of(Long groupId, Long id, List<Long> userIds) {
        List<ImGroupMember> groupMembers = new ArrayList<>(userIds.size() + 1);
        groupMembers.add(new ImGroupMember(groupId, id, GroupRole.CREATOR.role));
        userIds.forEach(userId -> groupMembers.add(new ImGroupMember(groupId, userId, GroupRole.COMMON.role)));
        return groupMembers;
    }
}
