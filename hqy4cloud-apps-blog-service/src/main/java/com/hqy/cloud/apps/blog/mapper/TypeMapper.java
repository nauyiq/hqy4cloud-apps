package com.hqy.cloud.apps.blog.mapper;

import com.hqy.cloud.apps.blog.entity.Type;
import com.hqy.cloud.db.tk.BaseTkMapper;
import org.springframework.stereotype.Repository;

/**
 * TypeDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:33
 */
@Repository
public interface TypeMapper extends BaseTkMapper<Type, Integer> {
}
