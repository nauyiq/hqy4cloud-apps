package com.hqy.cloud.apps.blog.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_chatgpt_message_history")
public class ChatgptMessageHistory extends BaseEntity<Long> {

    private Long id;
    private String chatId;
    private Long roleId;
    private Long userId;
    private String icon;
    private String type;
    private Integer token;
    private String content;
    private Boolean useContext;

    public ChatgptMessageHistory(Long userId) {
        this.userId = userId;
    }

    public ChatgptMessageHistory(String chatId, Long userId) {
        this.chatId = chatId;
        this.userId = userId;
    }
}
