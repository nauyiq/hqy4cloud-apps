package com.hqy.cloud.apps.blog.mapper;

import com.hqy.cloud.apps.blog.dto.StatisticsDTO;
import com.hqy.cloud.apps.blog.entity.Statistics;
import com.hqy.cloud.db.tk.BaseTkMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 9:28
 */
@Repository
public interface StatisticsMapper extends BaseTkMapper<Statistics, Long> {

    /**
     * 批量更新Statistics.
     * @param statistics
     */
    void updateStatistics(@Param("list") Collection<StatisticsDTO> statistics);
}
