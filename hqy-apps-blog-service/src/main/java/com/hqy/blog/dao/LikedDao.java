package com.hqy.blog.dao;

import com.hqy.base.BaseDao;
import com.hqy.blog.entity.Liked;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/31 11:07
 */
@Repository
public interface LikedDao extends BaseDao<Liked, Long> {

    /**
     * 新增或插入一条数据
     * @param liked like data.
     */
    void insertOrUpdate(@Param("liked") Liked liked);
}
