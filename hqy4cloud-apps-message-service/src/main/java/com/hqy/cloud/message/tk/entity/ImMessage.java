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
@Table(name = "t_im_message")
@EqualsAndHashCode(callSuper = true)
public class ImMessage extends BaseEntity<Long> {

    private String messageId;
    private Long conversationId;
    @Column(name = "is_group")
    private Boolean group;
    private Long from;
    private Long to;
    private String type;
    private String content;
    @Column(name = "is_read")
    private Boolean read;
    private Boolean status;

}
