package com.hqy.cloud.apps.blog.vo.chatgpt;

import com.hqy.cloud.apps.blog.entity.ChatgptConversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 14:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatgptConversationVO {

    private String userId;
    private String messageId;
    private Long roleId;
    private String icon;
    private String title;
    private String model;

    public ChatgptConversationVO(ChatgptConversation conversation) {
        this.userId = conversation.getUserId().toString();
        this.messageId = conversation.getMessageId().toString();
        this.roleId = conversation.getRoleId();
        this.icon = conversation.getIcon();
        this.title = conversation.getTitle();
        this.model = conversation.getModel();
    }
}
