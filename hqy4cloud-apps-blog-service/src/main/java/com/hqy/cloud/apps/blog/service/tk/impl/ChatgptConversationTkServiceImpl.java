package com.hqy.cloud.apps.blog.service.tk.impl;

import com.hqy.cloud.apps.blog.entity.ChatgptConversation;
import com.hqy.cloud.apps.blog.mapper.ChatgptConversationMapper;
import com.hqy.cloud.apps.blog.service.tk.ChatgptConversationTkService;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:28
 */
@Service
@RequiredArgsConstructor
public class ChatgptConversationTkServiceImpl extends BaseTkServiceImpl<ChatgptConversation, String> implements ChatgptConversationTkService {
    private final ChatgptConversationMapper mapper;

    @Override
    public BaseTkMapper<ChatgptConversation, String> getTkMapper() {
        return this.mapper;
    }
}
