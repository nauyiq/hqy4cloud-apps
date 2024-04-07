package com.hqy.cloud.message.db.entity;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import com.hqy.cloud.message.bind.dto.FileMessageDTO;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.enums.ImMessageState;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.util.JsonUtil;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 群聊消息表
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_im_group_message")
@EqualsAndHashCode(callSuper = true)
public class GroupMessage extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 发送人id
     */
    private Long send;

    /**
     * 群聊id
     */
    private Long groupId;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 内容
     */
    private String content;


    public static GroupMessage of(Long id, Long userId, Long groupId, Integer messageType, ImMessageDTO message) {
        GroupMessage groupMessage = GroupMessage.builder()
                .id(id)
                .send(userId)
                .groupId(groupId)
                .messageId(StringUtils.isNoneBlank(message.getMessageId()) ? message.getMessageId() : UUID.fastUUID().toString())
                .status(ImMessageState.SUCCESS.value)
                .type(messageType).build();

        boolean isFile = MessageType.isFileMessage(message.getType());
        // 文件消息, 存储文件消息JSON内容
        String content = isFile ? JsonUtil.toJson(new FileMessageDTO(message.getContent(), message.getFileSize(), message.getFileName())) : message.getContent();
        groupMessage.setContent(content);
        return groupMessage;
    }

    public static GroupMessage of(Long id, Long userId, Long groupId, Integer messageType, String message, Date date) {
        GroupMessage groupMessage = GroupMessage.builder()
                .id(id)
                .messageId(UUID.fastUUID().toString())
                .send(userId)
                .groupId(groupId)
                .content(message)
                .status(ImMessageState.SUCCESS.value)
                .type(messageType).build();
        if (date != null) {
            groupMessage.setCreated(date);
            groupMessage.setUpdated(date);
        }
        return groupMessage;
    }
}
