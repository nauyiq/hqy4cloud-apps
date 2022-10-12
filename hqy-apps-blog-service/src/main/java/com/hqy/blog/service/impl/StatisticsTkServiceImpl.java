package com.hqy.blog.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.blog.dao.StatisticsDao;
import com.hqy.blog.dto.StatisticsDTO;
import com.hqy.blog.entity.Statistics;
import com.hqy.blog.service.StatisticsTkService;
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
    private final StatisticsDao statisticsDao;

    @Override
    public BaseDao<Statistics, Long> selectDao() {
        return statisticsDao;
    }

    @Override
    public void updateStatistics(Collection<StatisticsDTO> values) {
        statisticsDao.updateStatistics(values);
    }
}
