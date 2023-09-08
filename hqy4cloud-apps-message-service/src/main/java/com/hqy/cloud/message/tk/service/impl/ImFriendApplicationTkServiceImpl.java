package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.PrimaryLessTkMapper;
import com.hqy.cloud.db.tk.support.PrimaryLessTkServiceImpl;
import com.hqy.cloud.message.tk.entity.ImFriendApplication;
import com.hqy.cloud.message.tk.mapper.ImFriendApplicationMapper;
import com.hqy.cloud.message.tk.service.ImFriendApplicationTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:24
 */
@Service
@RequiredArgsConstructor
public class ImFriendApplicationTkServiceImpl extends PrimaryLessTkServiceImpl<ImFriendApplication> implements ImFriendApplicationTkService {
    private final ImFriendApplicationMapper mapper;

    @Override
    public PrimaryLessTkMapper<ImFriendApplication> getTkDao() {
        return mapper;
    }

    @Override
    public int insertDuplicate(ImFriendApplication application) {
        return mapper.insertDuplicate(application);
    }

    @Override
    public List<ImFriendApplication> queryFriendApplications(Long userId) {
        return mapper.queryFriendApplications(userId);
    }
}
