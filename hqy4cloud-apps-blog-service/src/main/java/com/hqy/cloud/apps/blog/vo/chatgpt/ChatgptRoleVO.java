package com.hqy.cloud.apps.blog.vo.chatgpt;

import com.hqy.cloud.apps.blog.dto.chatgpt.ChatgptMessageContext;
import com.hqy.cloud.apps.blog.entity.ChatgptRole;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatgptRoleVO {

    private Long id;
    private String icon;
    private String key;
    private String helloMessage;
    private String name;
    private List<ChatgptMessageContext> context;

    public ChatgptRoleVO(ChatgptRole role) {
        AssertUtil.notNull(role, "Chatgpt role should not be null.");
        this.id = role.getId();
        this.icon = role.getIcon();
        this.key = role.getKey();
        this.helloMessage = role.getHelloMessage();
        this.name = role.getName();
        if (StringUtils.isNotBlank(role.getContext())) {
            this.context = JsonUtil.toList(role.getContext(), ChatgptMessageContext.class);
        }
    }
}
