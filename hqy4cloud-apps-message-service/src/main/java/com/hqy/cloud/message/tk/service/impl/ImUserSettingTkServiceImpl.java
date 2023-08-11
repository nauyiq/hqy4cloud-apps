package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.message.tk.entity.ImUserSetting;
import com.hqy.cloud.message.tk.mapper.ImUserSettingMapper;
import com.hqy.cloud.message.tk.service.ImUserSettingTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:07
 */
@Service
@RequiredArgsConstructor
public class ImUserSettingTkServiceImpl extends BaseTkServiceImpl<ImUserSetting, Long> implements ImUserSettingTkService {
    private final ImUserSettingMapper mapper;

    @Override
    public BaseTkMapper<ImUserSetting, Long> getTkMapper() {
        return mapper;
    }
}
