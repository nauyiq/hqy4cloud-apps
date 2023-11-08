package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.message.tk.entity.ImMessageHistory;
import com.hqy.cloud.message.tk.mapper.ImMessageHistoryMapper;
import com.hqy.cloud.message.tk.service.ImMessageHistoryTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:37
 */
@Service
@RequiredArgsConstructor
public class ImMessageHistoryTkServiceImpl extends BaseTkServiceImpl<ImMessageHistory, Long> implements ImMessageHistoryTkService {
    private final ImMessageHistoryMapper mapper;

    @Override
    public BaseTkMapper<ImMessageHistory, Long> getTkMapper() {
        return mapper;
    }
}
