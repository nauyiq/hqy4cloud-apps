package com.hqy.cloud.message.db.mapper;

import com.hqy.cloud.db.mybatisplus.BasePlusMapper;
import com.hqy.cloud.message.db.entity.Blacklist;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 黑名单表 Mapper 接口
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-04
 */
public interface BlacklistMapper extends BasePlusMapper<Blacklist> {

    /**
     * 查询用户id集合 根据userId 和 blackId
     * @param firstId  第一个id
     * @param secondId 第二个id
     * @return         用户id集合
     */
    List<Long> selectUserIds(@Param("firstId") Long firstId, @Param("secondId") Long secondId);
}
