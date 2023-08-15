package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.PrimaryLessTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.db.tk.support.PrimaryLessTkServiceImpl;
import com.hqy.cloud.message.tk.entity.ImContact;
import com.hqy.cloud.message.tk.mapper.ImContactMapper;
import com.hqy.cloud.message.tk.service.ImContactTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 13:24
 */
@Service
@RequiredArgsConstructor
public class ImContactTkServiceImpl extends BaseTkServiceImpl<ImContact, Long> implements ImContactTkService {
    private final ImContactMapper mapper;

    @Override
    public BaseTkMapper<ImContact, Long> getTkMapper() {
        return mapper;
    }
}
