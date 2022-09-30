package com.hqy.blog.dao;

import com.hqy.base.BaseDao;
import com.hqy.blog.entity.Tags;
import com.hqy.blog.entity.Type;
import org.springframework.stereotype.Repository;

/**
 * TagsDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:33
 */
@Repository
public interface TagsDao extends BaseDao<Tags, Integer> {
}
