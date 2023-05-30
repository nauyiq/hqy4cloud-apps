package com.hqy.cloud.apps.blog.service.impl;

import com.hqy.cloud.apps.blog.entity.Tags;
import com.hqy.cloud.apps.blog.mapper.TagsMapper;
import com.hqy.cloud.apps.blog.service.TagsTkService;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
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

    private final TagsMapper tagsMapper;

    @Override
    public BaseTkMapper<Tags, Integer> getTkMapper() {
        return tagsMapper;
    }
}
