package com.hqy.cloud.apps.blog.service.impl;

import com.hqy.cloud.apps.blog.service.StatisticsTkService;
import com.hqy.cloud.apps.blog.dto.StatisticsDTO;
import com.hqy.cloud.apps.blog.entity.Statistics;
import com.hqy.cloud.apps.blog.mapper.StatisticsMapper;
import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.cloud.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 9:28
 */
@Service
@RequiredArgsConstructor
public class StatisticsTkServiceImpl extends BaseTkServiceImpl<Statistics, Long> implements StatisticsTkService {

    private final StatisticsMapper mapper;

    @Override
    public BaseTkMapper<Statistics, Long> getTkMapper() {
        return mapper;
    }

    @Override
    public void updateStatistics(Collection<StatisticsDTO> values) {
        mapper.updateStatistics(values);
    }
}