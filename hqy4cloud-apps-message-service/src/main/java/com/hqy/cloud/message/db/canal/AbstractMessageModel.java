package com.hqy.cloud.message.db.canal;

import com.hqy.cloud.message.bind.dto.FileMessageDTO;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.es.document.ImMessage;
import com.hqy.cloud.util.JsonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/1
 */
@Data
public abstract class AbstractMessageModel {

    private Long id;
    private String messageId;
    private Integer type;
    private Integer status;
    private Long send;
    private String content;
    private Date created;
    private Date updated;

    public String buildId() {
        return this.id.toString() + type;
    }


    /**
     * 消息model转变成elastic文档对象
     * @return elastic文档对象
     */
    public ImMessage convertToMessageDoc() {
        ImMessage.ImMessageBuilder builder = ImMessage.builder()
                .id(buildId())
                .dbId(getId())
                .messageId(getMessageId())
                .isGroup(isGroupMessageModel())
                .receive(getReceiveId())
                .send(getSend())
                .type(getType())
                .status(getStatus())
                .created(getCreated() != null ? getCreated().getTime() : null);
        String content = getContent();
        if (MessageType.FILE.type.equals(getType()) || MessageType.IMAGE.type.equals(getType())) {
            if (StringUtils.isNotBlank(content)) {
                FileMessageDTO messageDTO = JsonUtil.toBean(content, FileMessageDTO.class);
                builder.path(messageDTO.getPath())
                        .fileName(messageDTO.getFileName())
                        .fileSize(messageDTO.getFileSize());
            }
        } else {
            builder.content(content);
        }
        return builder.build();
    }


    /**
     * 由子类返回是否是群聊消息
     * @return 是否是群聊消息
     */
    protected abstract boolean isGroupMessageModel();

    /**
     * 返回群聊id或者接收人ID
     * @return 群聊id或者接收人ID
     */
    protected abstract Long getReceiveId();



}
