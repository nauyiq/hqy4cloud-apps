package com.hqy.cloud.message.bind.dto;

import com.hqy.cloud.message.db.entity.PrivateConversation;
import com.hqy.cloud.message.db.entity.PrivateMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendPrivateMessageDTO {

    /**
     * 私聊聊天记录实体
     */
    private PrivateMessage privateMessage;

    /**
     * 我与好友的聊天会话实体
     */
    private List<PrivateConversation> conversations;


}
