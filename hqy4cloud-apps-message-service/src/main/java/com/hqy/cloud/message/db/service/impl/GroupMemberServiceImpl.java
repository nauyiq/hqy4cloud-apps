package com.hqy.cloud.message.db.service.impl;

import com.hqy.cloud.db.mybatisplus.BasePlusServiceImpl;
import com.hqy.cloud.message.bind.dto.*;
import com.hqy.cloud.message.db.entity.GroupMember;
import com.hqy.cloud.message.db.mapper.GroupMemberMapper;
import com.hqy.cloud.message.db.service.IGroupMemberService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * im群聊成员表 服务实现类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-05
 */
@Service
public class GroupMemberServiceImpl extends BasePlusServiceImpl<GroupMember, GroupMemberMapper> implements IGroupMemberService {

    @Override
    public boolean isGroupMember(Long groupId, Long userId) {
        Long id = baseMapper.selectIdByGroupIdAndUserId(groupId, userId);
        return id != null;
    }

    @Override
    public boolean insertOrUpdate(List<GroupMember> groupMembers) {
        return baseMapper.insertOrUpdate(groupMembers) > 0;
    }

    @Override
    public boolean removeMember(Long groupId, Long userId) {
        return baseMapper.removeMember(groupId, userId) > 0;
    }

    @Override
    public boolean realRemoveMember(Long groupId, Long userId) {
        return baseMapper.realRemoveMember(groupId, userId) > 0;
    }

    @Override
    public GroupMemberDTO getGroupMemberInfo(Long id, Long groupId) {
        return baseMapper.getGroupMemberInfoNotUsingDeleted(id, groupId);
    }

    @Override
    public Set<Long> getGroupMemberIds(Long groupId) {
        return baseMapper.getGroupMemberIds(groupId);
    }

    @Override
    public Set<ImUserInfoDTO> getGroupMemberUserInfo(Long groupId, Set<Long> groupMemberIds) {
        return baseMapper.getGroupMemberUserInfo(groupId, groupMemberIds);
    }

    @Override
    public List<GroupMemberIdsDTO> getGroupMembers(Set<Long> groupIds) {
        return baseMapper.getGroupMembers(groupIds);
    }

    @Override
    public List<ContactDTO> queryContactsByUserId(Long userId) {
        List<ContactDTO> contacts = baseMapper.queryContactsByUserId(userId);
        if (CollectionUtils.isNotEmpty(contacts)) {
            contacts.forEach(contact -> contact.setIsGroup(true));
        }
        return contacts;
    }

    @Override
    public List<GroupMemberInfoDTO> queryGroupMembers(Long groupId) {
        return baseMapper.queryGroupMembers(groupId);
    }


    @Override
    public GroupMember getByUserIdAndGroupId(Long id, Long groupId) {
        List<GroupMember> members = query().eq("user_id", id)
                .eq("group_id", groupId)
                .eq("deleted", 0).list();
        if (CollectionUtils.isEmpty(members)) {
            return null;
        }
        return members.get(0);
    }

    @Override
    public boolean updateTopState(Long userId, Long groupId, Boolean status) {
        return baseMapper.updateTopState(userId, groupId, status) > 0;
    }

    @Override
    public boolean updateNoticeState(Long userId, Long groupId, Boolean status) {
        return baseMapper.updateNoticeState(userId, groupId, status) > 0;
    }
}
