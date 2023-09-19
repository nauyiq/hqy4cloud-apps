package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.message.bind.dto.FriendApplicationDTO;
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
public class ImFriendApplicationTkServiceImpl extends BaseTkServiceImpl<ImFriendApplication, Long> implements ImFriendApplicationTkService {
    private final ImFriendApplicationMapper mapper;

    @Override
    public BaseTkMapper<ImFriendApplication, Long> getTkMapper() {
        return mapper;
    }

    @Override
    public boolean insertDuplicate(ImFriendApplication application) {
        return mapper.insertDuplicate(application) > 0;
    }

    @Override
    public List<ImFriendApplication> queryFriendApplications(Long userId) {
        return mapper.queryFriendApplications(userId);
    }

    @Override
    public boolean updateApplicationStatus(List<Long> ids, int status) {
        return mapper.updateApplicationStatus(ids, status) > 0;
    }

    @Override
    public FriendApplicationDTO queryApplicationStatus(Long id, Long userId) {
        ImFriendApplication application = mapper.selectOne(ImFriendApplication.of(userId, id));
        Integer status = null;
        if (application != null) {
            status = application.getStatus();
        }
        int unread = mapper.selectUnread(userId);
        return new FriendApplicationDTO(application == null ? null : application.getId(), unread, status);
    }
}
