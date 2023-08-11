package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.PrimaryLessBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 13:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_im_contact")
public class ImContact implements PrimaryLessBaseEntity {

    private Long id;
    private Long contactId;
    private Boolean group;
    private Boolean notice;
    private Boolean isTop;
    private String lastMessageType;
    private String lastMessageContent;
    private Date lastMessageTime;
    private Boolean status;
    private Date created;
    private Date updated;


}
