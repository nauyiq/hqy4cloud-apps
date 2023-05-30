package com.hqy.cloud.apps.blog.mapper;

import com.hqy.cloud.apps.blog.entity.Liked;
import com.hqy.cloud.db.tk.BaseTkMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/31 11:07
 */
@Repository
public interface LikedMapper extends BaseTkMapper<Liked, Long> {

    /**
     * 新增或插入一条数据
     * @param liked like data.
     */
    void insertOrUpdate(@Param("liked") Liked liked);
}
