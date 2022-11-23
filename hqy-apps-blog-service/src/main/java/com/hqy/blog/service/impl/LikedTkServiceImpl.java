package com.hqy.blog.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.blog.dao.LikedDao;
import com.hqy.blog.entity.Liked;
import com.hqy.blog.service.LikedTkService;
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

    private final LikedDao likedDao;

    @Override
    public BaseDao<Liked, Long> getTkDao() {
        return likedDao;
    }

    @Override
    public void insertOrUpdate(Liked liked) {
        likedDao.insertOrUpdate(liked);
    }
}
