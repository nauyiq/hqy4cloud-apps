package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.message.tk.entity.ImMessage;
import com.hqy.cloud.message.tk.mapper.ImMessageMapper;
import com.hqy.cloud.message.tk.service.ImMessageTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:37
 */
@Service
@RequiredArgsConstructor
public class ImMessageTkServiceImpl extends BaseTkServiceImpl<ImMessage, Long> implements ImMessageTkService {
    private final ImMessageMapper mapper;

    @Override
    public BaseTkMapper<ImMessage, Long> getTkMapper() {
        return mapper;
    }
}
