package com.hqy.cloud.apps.blog.mapper;

import com.hqy.cloud.apps.blog.entity.ChatgptConversation;
import com.hqy.cloud.db.tk.BaseTkMapper;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:24
 */
@Repository
public interface ChatgptConversationMapper extends BaseTkMapper<ChatgptConversation, String> {
}
