package com.hqy.cloud.message.db.entity;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.ImUserInfoDTO;
import com.hqy.cloud.message.bind.enums.MessageType;
import jodd.util.StringUtil;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 私聊聊天会话表
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
@TableName("t_im_private_conversation")
public class PrivateConversation extends BaseEntity implements Serializable {

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
     * 联系id
     */
    private Long contactId;

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
     * 展示名
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
     * 最后一次移除会话时间戳
     */
    private Long lastRemoveTime;

    /**
     * 是否删除
     */
    private Boolean deleted;


    public static PrivateConversation of(Long userId, Long contactId, Integer messageType, ImMessageDTO message) {
        PrivateConversation privateConversation = PrivateConversation.builder()
                .userId(userId)
                .contactId(contactId)
                .lastMessageTime(SystemClock.now())
                .lastMessageType(messageType)
                .deleted(false).build();

        String content = message.getContent();
        if (!MessageType.isFileMessage(messageType)) {
            content = content.length() > 250 ? content.substring(250) : content;
        }
        privateConversation.setLastMessageContent(content);
        return privateConversation;
    }



    public static List<PrivateConversation> ofList(Long userId, Long contactId, PrivateMessage message, Map<Long, ImUserInfoDTO> userInfoMap) {
        PrivateConversation fromConversation = getConversation(userId, contactId, message, userInfoMap);
        PrivateConversation toConversation = getConversation(contactId, userId, message, userInfoMap);
        return List.of(fromConversation, toConversation);
    }

    private static PrivateConversation getConversation(Long userId, Long contactId, PrivateMessage message, Map<Long, ImUserInfoDTO> userInfoMap) {
        ImUserInfoDTO userInfo = userInfoMap.get(contactId);
        String displayName = userInfo == null ? StrUtil.EMPTY : (StringUtil.isBlank(userInfo.getRemark()) ? userInfo.getUsername() : userInfo.getRemark()) ;
        return PrivateConversation.builder()
                .userId(userId)
                .contactId(contactId)
                .lastMessageTime(SystemClock.now())
                .lastMessageType(message.getType())
                .lastMessageContent(message.getContent())
                .notice(true)
                .top(false)
                .displayName(displayName)
                .deleted(false).build();
    }


    public static PrivateConversation of(Long userId, ConversationDTO conversation) {
        return PrivateConversation.builder()
                .id(conversation.getId())
                .userId(userId)
                .contactId(conversation.getContactId())
                .displayName(conversation.getDisplayName())
                .notice(conversation.getIsNotice())
                .top(conversation.getIsTop())
                .lastMessageContent(StrUtil.EMPTY)
                .lastMessageType(null)
                .lastMessageTime(null)
                .deleted(false).build();
    }
}
