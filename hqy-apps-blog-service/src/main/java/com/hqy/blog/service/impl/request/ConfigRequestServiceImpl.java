package com.hqy.blog.service.impl.request;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.base.common.base.lang.BaseMathConstants;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.entity.Config;
import com.hqy.blog.service.BlogDbOperationService;
import com.hqy.blog.service.request.ConfigRequestService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/11/4 13:32
 */
@Service
@RequiredArgsConstructor
public class ConfigRequestServiceImpl implements ConfigRequestService {

    private final BlogDbOperationService blogDbOperationService;

    private static final Cache<String, Config> CACHE = CacheBuilder.newBuilder().maximumSize(1).initialCapacity(1)
             .expireAfterWrite(BaseMathConstants.ONE_HOUR_4MILLISECONDS, TimeUnit.MILLISECONDS).build();

    private final String key =  ConfigRequestService.class.getSimpleName();

    @Override
    public DataResponse getAboutMe() {
        Config config = CACHE.getIfPresent(key);
        if (config == null) {
            List<Config> configs = blogDbOperationService.configTkService().queryAll();
            if (CollectionUtils.isEmpty(configs)) {
                return CommonResultCode.dataResponse();
            } else {
                CACHE.put(key, configs.get(0));
                return CommonResultCode.dataResponse(configs.get(0).getAboutMe());
            }
        } else {
          return   CommonResultCode.dataResponse(config.getAboutMe());
        }
    }



}
