package com.hqy.cloud.apps.blog.service.request.impl;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.cloud.apps.blog.entity.Config;
import com.hqy.cloud.apps.blog.service.opeations.BlogDbOperationService;
import com.hqy.cloud.apps.blog.service.request.ConfigRequestService;
import com.hqy.cloud.common.base.lang.NumberConstants;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.bind.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/11/4 13:32
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigRequestServiceImpl implements ConfigRequestService {
    private final BlogDbOperationService blogDbOperationService;
    private final NacosServiceManager nacosServiceManager;
    private final Environment environment;

    private static final Cache<String, Config> CACHE = CacheBuilder.newBuilder().maximumSize(1).initialCapacity(1)
             .expireAfterWrite(NumberConstants.ONE_HOUR_4MILLISECONDS, TimeUnit.MILLISECONDS).build();

    private final String key =  ConfigRequestService.class.getSimpleName();

    @Override
    public R<String> getAboutMe() {
        Config config = CACHE.getIfPresent(key);
        if (config == null) {
            List<Config> configs = blogDbOperationService.configTkService().queryAll();
            if (CollectionUtils.isEmpty(configs)) {
                return R.ok(StringConstants.EMPTY);
            } else {
                CACHE.put(key, configs.get(0));
                return R.ok(configs.get(0).getAboutMe());
            }
        } else {
          return R.ok(config.getAboutMe());
        }
    }
}
