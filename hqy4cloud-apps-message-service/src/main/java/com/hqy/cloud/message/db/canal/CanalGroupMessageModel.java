package com.hqy.cloud.message.db.canal;

import com.hqy.cloud.canal.annotation.CanalModel;
import com.hqy.cloud.canal.common.FieldNamingPolicy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * canal群聊聊天记录模型表
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@CanalModel(database = "apps_chat_message", table = "t_im_group_message", fieldNamingPolicy = FieldNamingPolicy.LOWER_UNDERSCORE)
public class CanalGroupMessageModel extends AbstractMessageModel {

    private Long groupId;

    @Override
    protected boolean isGroupMessageModel() {
        return true;
    }

    @Override
    protected Long getReceiveId() {
        return groupId;
    }
}
