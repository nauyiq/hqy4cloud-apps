package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_DEFAULT_GROUP_AVATAR;

/**
 * 群聊表 entity
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_im_group")
@EqualsAndHashCode(callSuper = true)
public class ImGroup extends BaseEntity<Long> {

    private String name;
    @Column(name = "`index`")
    private String index;
    private String avatar;
    private Long creator;
    private String notice;
    @Column(name = "is_invite")
    private Boolean invite = true;
    private Boolean status = true;

    public ImGroup(Long id) {
        super.setId(id);
    }

    public ImGroup(String name, Long creator) {
        this.name = name;
        this.creator = creator;
    }

    public static ImGroup of(Long groupId) {
        return new ImGroup(groupId);
    }

    public static ImGroup of(Long groupId, Boolean status) {
        ImGroup group = new ImGroup(groupId);
        group.setStatus(status);
        return group;
    }

    public static ImGroup of(String name, Long creator) {
        return new ImGroup(name, creator);
    }

    public static ImGroup ofAvatar(Long groupId, String avatar) {
        ImGroup group = of(groupId);
        group.setAvatar(avatar);
        return group;
    }

    public static ImGroup of(String name, Long creator, String avatar, Date now) {
        ImGroup imGroup = new ImGroup(name, creator);
        imGroup.setStatus(true);
        imGroup.setAvatar(StringUtils.isBlank(avatar) ? IM_DEFAULT_GROUP_AVATAR : avatar);
        imGroup.setCreated(now);
        imGroup.setUpdated(now);
        return imGroup;
    }


}
