package com.hqy.cloud.apps.blog.service.request.impl;

import com.hqy.cloud.apps.blog.service.tk.StatisticsTkService;
import com.hqy.cloud.apps.blog.service.request.StatisticRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/12 10:03
 */
@Service
@RequiredArgsConstructor
public class StatisticRequestServiceImpl implements StatisticRequestService {

    private final StatisticsTkService statisticsTkService;

    @Override
    public StatisticsTkService statisticsTkService() {
        return statisticsTkService;
    }
}
