package com.hqy.cloud.apps.blog.service.impl;

import com.hqy.cloud.apps.blog.entity.Liked;
import com.hqy.cloud.apps.blog.mapper.LikedMapper;
import com.hqy.cloud.apps.blog.service.LikedTkService;
import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.cloud.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/31 11:10
 */
@Service
@RequiredArgsConstructor
public class LikedTkServiceImpl extends BaseTkServiceImpl<Liked, Long> implements LikedTkService {

    private final LikedMapper likedMapper;

    @Override
    public BaseTkMapper<Liked, Long> getTkMapper() {
        return likedMapper;
    }

    @Override
    public void insertOrUpdate(Liked liked) {
        likedMapper.insertOrUpdate(liked);
    }
}
