package com.hqy.blog.service;

import com.hqy.base.BaseTkService;
import com.hqy.blog.entity.Liked;

/**
 * LikedTkService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/31 11:10
 */
public interface LikedTkService extends BaseTkService<Liked, Long> {

    /**
     * 新增或插入一条数据
     * @param liked  liked entity.
     */
    void insertOrUpdate(Liked liked);
}
