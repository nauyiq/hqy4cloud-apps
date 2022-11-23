package com.hqy.blog.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.blog.dao.ConfigDao;
import com.hqy.blog.entity.Config;
import com.hqy.blog.service.ConfigTkService;
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

    private final ConfigDao configDao;

    @Override
    public BaseDao<Config, Integer> getTkDao() {
        return configDao;
    }
}
