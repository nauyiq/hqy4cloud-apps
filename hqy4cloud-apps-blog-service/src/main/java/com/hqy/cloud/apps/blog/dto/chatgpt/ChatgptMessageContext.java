package com.hqy.cloud.apps.blog.dto.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatgptMessageContext {
    private String role;
    private String content;


}
