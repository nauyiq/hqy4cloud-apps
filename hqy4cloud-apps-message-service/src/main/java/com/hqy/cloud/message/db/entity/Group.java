package com.hqy.cloud.message.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * im群聊表
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_im_group")
@EqualsAndHashCode(callSuper = true)
public class Group extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 群聊id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 群聊名
     */
    private String name;

    /**
     * 创建者
     */
    private Long creator;

    /**
     * 群头像
     */
    private String avatar;

    /**
     * 群公告
     */
    private String notice;

    /**
     * 是否允许邀请群成员
     */
    private Boolean isInvite;

    /**
     * 状态,是否可用
     */
    private Boolean status;

    /**
     * 是否删除
     */
    private Boolean deleted;

    public static Group of(String groupName, Long creator, String avatar, GroupDTO createGroup) {
        Group group = Group.builder()
                .name(groupName)
                .avatar(avatar)
                .creator(creator)
                .notice(createGroup.getNotice())
                .deleted(false)
                .isInvite(true).build();
        Date date = new Date();
        group.setCreated(date);
        group.setUpdated(date);
        return group;
    }
}
