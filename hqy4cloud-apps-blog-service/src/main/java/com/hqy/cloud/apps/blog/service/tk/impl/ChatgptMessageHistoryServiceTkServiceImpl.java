package com.hqy.cloud.apps.blog.service.tk.impl;

import com.hqy.cloud.apps.blog.entity.ChatgptMessageHistory;
import com.hqy.cloud.apps.blog.mapper.ChatgptMessageHistoryMapper;
import com.hqy.cloud.apps.blog.service.tk.ChatgptMessageHistoryTkService;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:30
 */
@Service
@RequiredArgsConstructor
public class ChatgptMessageHistoryServiceTkServiceImpl extends BaseTkServiceImpl<ChatgptMessageHistory, Long> implements ChatgptMessageHistoryTkService {
    private final ChatgptMessageHistoryMapper mapper;

    @Override
    public BaseTkMapper<ChatgptMessageHistory, Long> getTkMapper() {
        return this.mapper;
    }
}
