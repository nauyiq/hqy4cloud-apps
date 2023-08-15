package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.tk.entity.ImGroup;
import com.hqy.cloud.message.tk.mapper.ImGroupMapper;
import com.hqy.cloud.message.tk.service.ImGroupTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:28
 */
@Service
@RequiredArgsConstructor
public class ImGroupTkServiceImpl extends BaseTkServiceImpl<ImGroup, Long> implements ImGroupTkService {
    private final ImGroupMapper mapper;

    @Override
    public BaseTkMapper<ImGroup, Long> getTkMapper() {
        return mapper;
    }

    @Override
    public GroupMemberDTO getGroupMemberInfo(Long id, Long groupId) {
        return mapper.getGroupMemberInfo(id, groupId);
    }
}
