package com.hqy.cloud.apps.blog.service;

import com.hqy.cloud.apps.blog.dto.StatisticsDTO;
import com.hqy.cloud.apps.blog.entity.Statistics;
import com.hqy.cloud.tk.BaseTkService;

import java.util.Collection;

/**
 * StatisticsTkService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 9:28
 */
public interface StatisticsTkService extends BaseTkService<Statistics, Long> {

    /**
     * 批量更新统计数据.
     * @param values StatisticsDTO.
     */
    void updateStatistics(Collection<StatisticsDTO> values);
}
