package com.hqy.cloud.apps.blog.service.tk.impl;

import com.hqy.cloud.apps.blog.entity.ArticleUserState;
import com.hqy.cloud.apps.blog.mapper.ArticleUserStateMapper;
import com.hqy.cloud.apps.blog.service.tk.ArticleUserStateTkService;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/22 11:40
 */
@Service
@RequiredArgsConstructor
public class ArticleUserStateTkServiceImpl extends BaseTkServiceImpl<ArticleUserState, Long> implements ArticleUserStateTkService {
    private final ArticleUserStateMapper mapper;

    @Override
    public BaseTkMapper<ArticleUserState, Long> getTkMapper() {
        return mapper;
    }

    @Override
    public boolean insertOrUpdate(List<ArticleUserState> states) {
        return mapper.insertOrUpdate(states) > 0;
    }
}
