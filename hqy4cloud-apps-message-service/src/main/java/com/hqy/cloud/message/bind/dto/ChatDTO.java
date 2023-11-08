package com.hqy.cloud.message.bind.dto;

import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.entity.ImFriend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/22 10:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {

    private ImConversation conversation;
    private ImFriend friend;
    private GroupContactDTO groupContact;

}
