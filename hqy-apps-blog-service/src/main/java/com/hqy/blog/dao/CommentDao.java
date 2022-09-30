package com.hqy.blog.dao;

import com.hqy.base.BaseDao;
import com.hqy.blog.entity.Comment;
import org.springframework.stereotype.Repository;

/**
 * CommentDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:29
 */
@Repository
public interface CommentDao extends BaseDao<Comment, Long> {
}
