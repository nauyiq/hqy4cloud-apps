package com.hqy.cloud.message.service.request.impl;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.ConvertUtil;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.bind.enums.GroupRole;
import com.hqy.cloud.message.bind.vo.GroupMemberVO;
import com.hqy.cloud.message.service.ImGroupOperationsService;
import com.hqy.cloud.message.service.request.ImGroupRequestService;
import com.hqy.cloud.message.tk.entity.ImFriend;
import com.hqy.cloud.message.tk.entity.ImGroupMember;
import com.hqy.cloud.message.tk.service.ImFriendTkService;
import com.hqy.cloud.message.tk.service.ImGroupMemberTkService;
import com.hqy.cloud.message.tk.service.ImGroupTkService;
import com.hqy.cloud.web.common.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.result.AppsResultCode.IM_GROUP_NOT_EXIST;
import static com.hqy.cloud.apps.commom.result.AppsResultCode.IM_NOT_GROUP_MEMBER;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:38
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImGroupRequestServiceImpl implements ImGroupRequestService {
    private final ImFriendTkService imFriendTkService;
    private final ImGroupTkService groupTkService;
    private final ImGroupMemberTkService groupMemberTkService;
    private final ImGroupOperationsService groupOperationsService;

    @Override
    public R<Boolean> createGroup(Long creator, GroupDTO createGroup) {
        // 判断输入的用户ids是否都是好友.
        List<Long> friendIds = createGroup.getUserIds();
        List<ImFriend> friends = imFriendTkService.queryFriends(creator, friendIds);
        if (CollectionUtils.isEmpty(friends) || friends.size() != friendIds.size()) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        return groupOperationsService.createGroup(creator, createGroup, friends) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> editGroup(AuthenticationInfo info, GroupDTO editGroup) {
        GroupMemberDTO groupMemberInfo = groupTkService.getGroupMemberInfo(info.getId(), editGroup.getGroupId());
        if (groupMemberInfo == null) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }
        if (groupMemberInfo.getRole() == null || groupMemberInfo.getDeleted()) {
            return R.failed(ResultCode.NOT_PERMISSION);
        }
        return groupOperationsService.editGroup(info, groupMemberInfo, editGroup) ? R.ok() : R.failed();
    }

    @Override
    public R<List<GroupMemberVO>> getGroupMembers(Long userId, Long groupId) {
        ImGroupMember of = ImGroupMember.of(groupId);
        of.setDeleted(null);
        List<ImGroupMember> groupMembers = groupMemberTkService.queryList(of);
        if (CollectionUtils.isEmpty(groupMembers)) {
            return R.ok(Collections.emptyList());
        }
        Map<Long, ImGroupMember> memberMap = groupMembers.stream().collect(Collectors.toMap(ImGroupMember::getUserId, e -> e));
        if (!memberMap.containsKey(userId)) {
            // 不是群成员用户不应该返回群成员列表.
            return R.ok(Collections.emptyList());
        }
        Map<Long, AccountProfileStruct> infos = AccountRpcUtil.getAccountProfileMap(new ArrayList<>(memberMap.keySet()));
        if (MapUtil.isEmpty(infos)) {
            return R.ok(Collections.emptyList());
        }
        ImGroupMember self = memberMap.get(userId);
        if (self.getDeleted()) {
            //如果已被移除，则只显示群主.
            groupMembers = groupMembers.stream().filter(member -> member.getRole().equals(GroupRole.CREATOR.role)).toList();
        }
        groupMembers = groupMembers.stream().filter(member -> !member.getDeleted()).toList();
        return R.ok(ConvertUtil.convertGroupMembers(groupMembers, infos));
    }

    @Override
    public R<Boolean> addGroupMember(Long id, GroupDTO group) {
        Long groupId = group.getGroupId();
        List<Long> userIds = group.getUserIds().stream().distinct().toList();
        GroupMemberDTO groupMemberInfo = groupTkService.getGroupMemberInfo(id, groupId);
        if (groupMemberInfo == null) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }
        Integer role = groupMemberInfo.getRole();
        if (role == null || !role.equals(GroupRole.CREATOR.role)) {
            return R.failed(ResultCode.NOT_PERMISSION);
        }
        return groupOperationsService.addGroupMember(groupMemberInfo, userIds) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> editGroupMember(Long id, GroupMemberDTO groupMember) {
        GroupMemberDTO groupMemberInfo = groupTkService.getGroupMemberInfo(id, groupMember.getGroupId());
        if (groupMemberInfo == null) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }

        ImGroupMember imGroupMember = new ImGroupMember(groupMember.getGroupId(), groupMember.getId());
        Integer role = groupMember.getRole();
        //update member role
        if (role != null) {
            if (!GroupRole.enableRole(role) || !groupMemberInfo.getRole().equals(GroupRole.CREATOR.role)) {
                return R.failed(ResultCode.NOT_PERMISSION);
            }
            imGroupMember.setRole(role);
        }
        //update member display name
        String displayName = groupMember.getDisplayName();
        if (StringUtils.isNotBlank(displayName)) {
            imGroupMember.setDisplayName(displayName);
        }
        return groupMemberTkService.updateSelective(imGroupMember) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> removeGroupMember(Long id, GroupMemberDTO groupMember) {
        GroupMemberDTO groupMemberInfo = groupTkService.getGroupMemberInfo(id, groupMember.getGroupId());
        if (groupMemberInfo == null) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }
        Integer role = groupMemberInfo.getRole();
        if (!role.equals(GroupRole.CREATOR.role) && !id.equals(groupMember.getId()) ) {
            return R.failed(ResultCode.NOT_PERMISSION);
        }
        return groupOperationsService.removeGroupMember(id, groupMember.getId(), groupMember.getGroupId()) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> exitGroup(Long userId, Long groupId) {
        ImGroupMember member = groupMemberTkService.queryOne(ImGroupMember.of(groupId, userId));
        if (member == null || member.getRole().equals(GroupRole.CREATOR.role) || member.getDeleted()) {
            return R.failed(IM_NOT_GROUP_MEMBER);
        }
        return groupOperationsService.exitGroup(member) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> deleteGroup(Long userId, Long groupId) {
        GroupMemberDTO groupMemberInfo = groupTkService.getGroupMemberInfo(userId, groupId);
        if (groupMemberInfo == null) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }
        Integer role = groupMemberInfo.getRole();
        if (!role.equals(GroupRole.CREATOR.role)) {
            return R.failed(ResultCode.NOT_PERMISSION);
        }
        return groupOperationsService.deleteGroup(userId, groupId) ? R.ok() : R.failed();
    }
}
