package com.hqy.cloud.apps.blog.service.impl;

import com.hqy.cloud.apps.blog.entity.Config;
import com.hqy.cloud.apps.blog.mapper.ConfigMapper;
import com.hqy.cloud.apps.blog.service.ConfigTkService;
import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.cloud.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigTkServiceImpl extends BaseTkServiceImpl<Config, Integer> implements ConfigTkService {

    private final ConfigMapper configMapper;

    @Override
    public BaseTkMapper<Config, Integer> getTkMapper() {
        return configMapper;
    }
}
