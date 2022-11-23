package com.hqy.blog.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.blog.dao.TagsDao;
import com.hqy.blog.dao.TypeDao;
import com.hqy.blog.entity.Tags;
import com.hqy.blog.entity.Type;
import com.hqy.blog.service.TagsTkService;
import com.hqy.blog.service.TypeTkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 11:34
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagsTkServiceImpl extends BaseTkServiceImpl<Tags, Integer> implements TagsTkService {

    private final TagsDao tagsDao;

    @Override
    public BaseDao<Tags, Integer> getTkDao() {
        return tagsDao;
    }
}
