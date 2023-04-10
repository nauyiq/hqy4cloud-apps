package com.hqy.cloud.apps.blog.service;

import com.github.pagehelper.PageInfo;
import com.hqy.cloud.apps.blog.entity.Type;
import com.hqy.cloud.tk.BaseTkService;

/**
 * TypeTkService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:34
 */
public interface TypeTkService extends BaseTkService<Type, Integer> {

    /**
     * 获取type表的分页结果
     * @param name    模糊查询-类型名
     * @param current 当前页
     * @param size    页行数
     * @return        PageInfo.
     */
    PageInfo<Type> queryPageTypes(String name, Integer current, Integer size);
}
