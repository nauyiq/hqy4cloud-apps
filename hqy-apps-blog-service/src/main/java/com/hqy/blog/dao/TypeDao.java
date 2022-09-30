package com.hqy.blog.dao;

import com.hqy.base.BaseDao;
import com.hqy.blog.entity.Type;
import org.springframework.stereotype.Repository;

/**
 * TypeDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:33
 */
@Repository
public interface TypeDao extends BaseDao<Type, Integer> {
}
