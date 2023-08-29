package com.hqy.cloud.message.canal.model;

import com.hqy.cloud.canal.annotation.CanalModel;
import com.hqy.cloud.canal.common.FieldNamingPolicy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/25 11:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@CanalModel(database = "apps_message", table = "t_im_conversation", fieldNamingPolicy = FieldNamingPolicy.LOWER_UNDERSCORE)
public class CanalImConversation {

    private Long id;
    private Long userId;
    private Long contactId;
    private Integer isGroup;
    private Integer isNotice;
    private Integer isTop;
    private Integer isRemove;
    private Integer lastMessageFrom;
    private String lastMessageType;
    private String lastMessageContent;
    private Date lastMessageTime;
    private Date created;
    private Date updated;


}
