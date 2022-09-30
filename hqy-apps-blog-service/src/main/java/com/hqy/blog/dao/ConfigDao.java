package com.hqy.blog.dao;

import com.hqy.base.BaseDao;
import com.hqy.blog.entity.Config;
import org.springframework.stereotype.Repository;

/**
 * ConfigDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:42
 */
@Repository
public interface ConfigDao extends BaseDao<Config, Integer> {
}
