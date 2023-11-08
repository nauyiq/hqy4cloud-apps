package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.tk.entity.ImGroupMember;
import com.hqy.cloud.message.tk.mapper.ImGroupMemberMapper;
import com.hqy.cloud.message.tk.service.ImGroupMemberTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:31
 */
@Service
@RequiredArgsConstructor
public class ImGroupMemberTkServiceImpl extends BaseTkServiceImpl<ImGroupMember,Long> implements ImGroupMemberTkService {
    private final ImGroupMemberMapper mapper;

    @Override
    public BaseTkMapper<ImGroupMember, Long> getTkMapper() {
        return mapper;
    }

    @Override
    public boolean insertOrUpdate(List<ImGroupMember> members) {
        return mapper.insertOrUpdate(members) > 0;
    }

    @Override
    public List<GroupMemberDTO> queryMembers(Long id, List<Long> groupIds) {
        return mapper.queryMembers(id, groupIds);
    }

    @Override
    public List<ImGroupMember> queryGroupMembers(Long groupId, List<Long> userIds) {
        return mapper.queryGroupMembers(groupId, userIds);
    }

    @Override
    public Boolean updateMember(ImGroupMember member) {
        return mapper.updateMember(member) > 0;
    }

    @Override
    public List<ImGroupMember> simpleQueryAllGroupMembers(Long groupId) {
        if (groupId == null) {
            return Collections.emptyList();
        }
        return mapper.simpleQueryAllGroupMembers(groupId);
    }

    @Override
    public boolean removeGroupMember(Long groupId, Long userId) {
        if (groupId == null) {
            return false;
        }
        return mapper.removeGroupMember(groupId, userId) > 0;
    }

    @Override
    public List<GroupDTO> queryGroupMembersByGroupIds(List<Long> groupIds) {
        return mapper.queryGroupMembersByGroupIds(groupIds);
    }
}
