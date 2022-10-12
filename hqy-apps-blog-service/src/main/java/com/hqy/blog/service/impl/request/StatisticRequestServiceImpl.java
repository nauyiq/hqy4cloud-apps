package com.hqy.blog.service.impl.request;

import com.hqy.blog.service.StatisticsTkService;
import com.hqy.blog.service.request.StatisticRequestService;
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
