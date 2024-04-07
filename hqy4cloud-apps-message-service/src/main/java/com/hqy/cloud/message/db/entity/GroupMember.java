package com.hqy.cloud.message.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import com.hqy.cloud.message.bind.dto.AddGroupMemberDTO;
import com.hqy.cloud.message.bind.enums.GroupRole;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * im群聊成员表
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-05
 */
@Data
@Builder
@TableName("t_im_group_member")
@EqualsAndHashCode(callSuper = true)
public class GroupMember extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 当前用户id
     */
    private Long groupId;

    /**
     * 好友用户id
     */
    private Long userId;

    /**
     * 在群的展示名字
     */
    private String displayName;

    /**
     * 群角色
     */
    private Integer role;

    /**
     * 是否消息置顶
     */
    @TableField(value = "is_top")
    private Boolean top;

    /**
     * 是否消息通知
     */
    @TableField(value = "is_notice")
    private Boolean notice;

    /**
     * 是否删除
     */
    private Boolean deleted;

    public static List<GroupMember> of(Long creator, List<Long> ids, Long groupId, Map<Long, UserSetting> map, Date date) {
        return ids.stream().map(id -> {
             GroupMember member = GroupMember.builder()
                    .userId(id)
                    .groupId(groupId)
                    .displayName(map.get(id).getUsername())
                    .top(false)
                    .notice(true)
                    .role(id.equals(creator) ? GroupRole.CREATOR.role : GroupRole.COMMON.role)
                    .deleted(false).build();
            if (date != null) {
                member.setCreated(date);
                member.setUpdated(date);
            }
            return member;
        }).toList();
    }


    public static List<GroupMember> of(List<Long> ids, Long groupId, Map<Long, AddGroupMemberDTO> map) {
        return ids.stream().map(id -> GroupMember.builder()
                .userId(id)
                .groupId(groupId)
                .displayName(map.get(id).getUsername())
                .top(false)
                .notice(true)
                .role(GroupRole.COMMON.role)
                .deleted(false).build()).toList();
    }
}
