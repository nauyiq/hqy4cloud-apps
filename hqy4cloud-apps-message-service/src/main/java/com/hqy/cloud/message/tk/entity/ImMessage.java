package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_im_message")
@EqualsAndHashCode(callSuper = true)
public class ImMessage extends BaseEntity<Long> {

    private String messageId;
    @Column(name = "is_group")
    private Boolean group;
    @Column(name = "`from`")
    private Long from;
    @Column(name = "`to`")
    private Long to;
    private String type;
    private String content;
    private String fileName;
    private Long fileSize;
    @Column(name = "is_read")
    private Boolean read;
    private Boolean status;

    public ImMessage(long id, Date date, String messageId, Boolean group, Long from, Long to, String type, String content) {
        super(id, date);
        this.messageId = messageId;
        this.group = group;
        this.from = from;
        this.to = to;
        this.type = type;
        this.content = content;
        this.read = false;
        this.status = true;
    }


    public static ImMessage of(long id, Long from, ImMessageDTO message) {
        Long sendTime = message.getSendTime();
        Date now = sendTime == null ? new Date() : new Date(sendTime);
        ImMessage imMessage = new ImMessage(id, now, message.getId(), message.getIsGroup(), from, Long.valueOf(message.getToContactId()), message.getType(), message.getContent());
        imMessage.setFileName(message.getFileName());
        imMessage.setFileSize(message.getFileSize());
        return imMessage;
    }
}
