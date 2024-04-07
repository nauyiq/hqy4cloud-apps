package com.hqy.cloud.message.db.entity;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import com.hqy.cloud.message.bind.dto.FileMessageDTO;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.enums.ImMessageState;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.util.JsonUtil;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 私聊聊天消息表
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_im_private_message")
@EqualsAndHashCode(callSuper = true)
public class PrivateMessage extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 发送人ID
     */
    private Long send;

    /**
     * 接收人ID
     */
    private Long receive;

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 消息状态
     */
    private Integer status;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 消息内容
     */
    private String content;


    public static PrivateMessage of(Long id, Long userId, Long friendId, Integer messageType, ImMessageDTO message) {
        PrivateMessage privateMessage = PrivateMessage.builder()
                .id(id)
                .messageId(message.getId())
                .send(userId)
                .receive(friendId)
                .isRead(false)
                .status(ImMessageState.SUCCESS.value)
                .type(messageType).build();


        boolean isFile = MessageType.isFileMessage(message.getType());
        // 文件消息, 存储文件消息JSON内容
        String content = isFile ? JsonUtil.toJson(new FileMessageDTO(message.getContent(), message.getFileSize(), message.getFileName())) : message.getContent();
        privateMessage.setContent(content);
        Date now = new Date();
        privateMessage.setCreated(now);
        privateMessage.setUpdated(now);
        return privateMessage;
    }

    public static PrivateMessage of(Long id, Long userId, Long friendId, Integer messageType, String message, Date date) {
        PrivateMessage privateMessage = PrivateMessage.builder()
                .id(id)
                .messageId(UUID.fastUUID().toString())
                .send(userId)
                .receive(friendId)
                .isRead(false)
                .content(message)
                .status(ImMessageState.SUCCESS.value)
                .type(messageType).build();
        if (date != null) {
            privateMessage.setCreated(date);
            privateMessage.setUpdated(date);
        } else {
            Date now = new Date();
            privateMessage.setCreated(now);
            privateMessage.setUpdated(now);
        }
        return privateMessage;
    }

}
