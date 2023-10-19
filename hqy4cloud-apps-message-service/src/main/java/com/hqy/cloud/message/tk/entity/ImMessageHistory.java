package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_im_message_history")
public class ImMessageHistory extends BaseEntity<Long> {

    private Long imMessageId;
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

}
