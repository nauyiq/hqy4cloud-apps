package com.hqy.blog.dao;

import com.hqy.base.BaseDao;
import com.hqy.blog.dto.StatisticsDTO;
import com.hqy.blog.entity.Statistics;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 9:28
 */
@Repository
public interface StatisticsDao extends BaseDao<Statistics, Long> {

    /**
     * 批量更新Statistics.
     * @param statistics
     */
    void updateStatistics(@Param("list") Collection<StatisticsDTO> statistics);
}
