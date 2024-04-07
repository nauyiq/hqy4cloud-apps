package com.hqy.cloud.message.db.canal;

import com.hqy.cloud.canal.annotation.CanalModel;
import com.hqy.cloud.canal.common.FieldNamingPolicy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * canal私聊聊天记录模型表
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@CanalModel(database = "apps_chat_message", table = "t_im_private_message", fieldNamingPolicy = FieldNamingPolicy.LOWER_UNDERSCORE)
public class CanalPrivateMessageModel extends AbstractMessageModel {

    private Long receive;
    private Integer isRead;

    @Override
    protected boolean isGroupMessageModel() {
        return false;
    }

    @Override
    protected Long getReceiveId() {
        return receive;
    }
}
