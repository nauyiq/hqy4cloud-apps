package com.hqy.cloud.apps.blog.mapper;

import com.hqy.cloud.apps.blog.entity.Tags;
import com.hqy.cloud.tk.BaseTkMapper;
import org.springframework.stereotype.Repository;

/**
 * TagsDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:33
 */
@Repository
public interface TagsMapper extends BaseTkMapper<Tags, Integer> {
}
