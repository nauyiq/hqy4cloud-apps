package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.mapper.ImConversationMapper;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 13:24
 */
@Service
@RequiredArgsConstructor
public class ImConversationTkServiceImpl extends BaseTkServiceImpl<ImConversation, Long> implements ImConversationTkService {
    private final ImConversationMapper mapper;

    @Override
    public BaseTkMapper<ImConversation, Long> getTkMapper() {
        return mapper;
    }
}
