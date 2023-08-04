package com.hqy.cloud.apps.blog.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 11:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_chatgpt_list")
public class ChatgptConversation extends BaseEntity<String> {

    private Long userId;
    private Long messageId;
    private String icon;
    private String title;
    private String model;
    private Long roleId;

    public ChatgptConversation(String chatId) {
        super.setId(chatId);
    }

    public ChatgptConversation(Long userId) {
        this.userId = userId;
    }
}
