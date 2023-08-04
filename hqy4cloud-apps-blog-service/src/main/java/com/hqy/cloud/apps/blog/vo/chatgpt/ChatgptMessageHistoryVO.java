package com.hqy.cloud.apps.blog.vo.chatgpt;

import com.hqy.cloud.apps.blog.entity.ChatgptMessageHistory;
import com.hqy.cloud.util.AssertUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 15:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatgptMessageHistoryVO {

    private String id;
    private Long roleId;
    private String userId;
    private String chatId;
    private String icon;
    private String type;
    private Integer tokens;
    private Boolean useContext;
    private String content;

    public ChatgptMessageHistoryVO(ChatgptMessageHistory history) {
        AssertUtil.notNull(history, "Chatgpt message should not be null.");
        this.id = history.getId().toString();
        this.roleId = history.getRoleId();
        this.userId = history.getUserId().toString();
        this.chatId = history.getChatId();
        this.icon = history.getIcon();
        this.type = history.getType();
        this.tokens = history.getToken();
        this.useContext = history.getUseContext();
        this.content = history.getContent();
    }
}
