package com.hqy.cloud.message.db.entity;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.message.bind.enums.GroupRole;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 群聊聊天会话表
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_im_group_conversation")
public class GroupConversation extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 群id
     */
    private Long groupId;

    /**
     * 是否消息提醒
     */
    @TableField(value = "is_notice")
    private Boolean notice;

    /**
     * 是否置顶
     */
    @TableField(value = "is_top")
    private Boolean top;

    /**
     * 群聊角色
     */
    private Integer role;

    /**
     * 群聊展示名字
     */
    private String displayName;

    /**
     * 最后一条消息类型
     */
    private Integer lastMessageType;

    /**
     * 最后一条消息
     */
    private String lastMessageContent;

    /**
     * 最后一条消息时间
     */
    private Long lastMessageTime;

    /**
     * 最后一次移除会话时间
     */
    private Long lastRemoveTime;

    /**
     * 上次读取消息时间
     */
    private Long lastReadTime;

    /**
     * 是否删除
     */
    private Boolean deleted;

    public static GroupConversation of(Long userId, ConversationDTO conversation) {
        return GroupConversation.builder()
                .id(conversation.getId())
                .groupId(conversation.getContactId())
                .userId(userId)
                .role(conversation.getRole())
                .displayName(conversation.getDisplayName())
                .top(conversation.getIsTop())
                .notice(conversation.getIsNotice())
                .lastMessageContent(StrUtil.EMPTY)
                .lastMessageType(null)
                .lastMessageTime(null)
                .deleted(false).build();
    }

    public static List<GroupConversation> of(Long creator, List<Long> ids, Long groupId, Map<Long, UserSetting> map) {
        long now = SystemClock.now();
        return ids.stream().map(id -> GroupConversation.builder()
                .groupId(groupId)
                .userId(id)
                .notice(true)
                .displayName(map.get(id).getUsername())
                .top(false)
                .lastMessageTime(now)
                .lastReadTime(now)
                .lastRemoveTime(now)
                .lastMessageType(EventMessageType.EVENT_CREATE_GROUP.type)
                .role(id.equals(creator) ? GroupRole.CREATOR.role : GroupRole.COMMON.role)
                .deleted(false).build()).toList();
    }

    public static List<GroupConversation> of(List<GroupMember> groupMembers) {
        long now = SystemClock.now();
        return groupMembers.stream().map(member -> GroupConversation.builder()
                .groupId(member.getGroupId())
                .userId(member.getUserId())
                .notice(member.getNotice())
                .displayName(member.getDisplayName())
                .top(member.getTop())
                .role(member.getRole())
                .lastMessageTime(now)
                .lastReadTime(now)
                .lastRemoveTime(now)
                .deleted(false).build()).toList();
    }

    /**
     * 判断是否存在未读消息
     * @return  是否存在存在未读消息
     */
    public boolean hasUnreadMessage() {
        if (lastMessageTime == null) {
            return false;
        }
        if (lastReadTime == null) {
            return true;
        }
        return lastMessageTime > lastReadTime;
    }



}
