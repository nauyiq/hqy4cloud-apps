package com.hqy.cloud.message.service.impl;

import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.foundation.common.account.AccountAvatarUtil;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberInfoDTO;
import com.hqy.cloud.message.bind.enums.GroupRole;
import com.hqy.cloud.message.bind.vo.GroupMemberVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.db.entity.UserSetting;
import com.hqy.cloud.message.db.service.IGroupMemberService;
import com.hqy.cloud.message.db.service.IUserSettingService;
import com.hqy.cloud.message.service.ImGroupService;
import com.hqy.cloud.message.service.ImUserRelationshipService;
import com.hqy.cloud.message.service.request.ImGroupRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.hqy.cloud.apps.commom.result.AppsResultCode.IM_GROUP_NOT_EXIST;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImGroupRequestServiceImpl implements ImGroupRequestService {
    private final ImGroupService groupService;
    private final IGroupMemberService iGroupMemberService;
    private final IUserSettingService userSettingService;
    private final ImUserRelationshipService userRelationshipService;


    @Override
    public R<Boolean> createGroup(Long creator, GroupDTO createGroup) {
        // 判断输入的用户ids是否都是好友.
        List<Long> userIds = createGroup.getUserIds();
        if (!userRelationshipService.allFriend(creator, userIds)) {
            return R.failed(AppsResultCode.IM_NOT_FRIEND);
        }
        // 查找用户信息
        List<Long> friendIds = new ArrayList<>(userIds);
        friendIds.add(creator);
        List<UserSetting> userSettings = userSettingService
                .listByIds(new HashSet<>(friendIds));
        if (CollectionUtils.isEmpty(userSettings) || userSettings.size() != friendIds.size()) {
            return R.failed(ResultCode.USER_NOT_FOUND);
        }
        return groupService.createGroup(creator, createGroup, userSettings) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> editGroup(Long id, String username, GroupDTO editGroup) {
        Long groupId = editGroup.getGroupId();
        // 判断是否是群成员，并且是否有权力修改群名
        GroupMemberDTO groupMemberInfo = iGroupMemberService.getGroupMemberInfo(id, groupId);
        if (groupMemberInfo == null || groupMemberInfo.getDeleted()) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }
        if (groupMemberInfo.getRole() == null || groupMemberInfo.getRole().equals(GroupRole.COMMON.role)) {
            return R.failed(ResultCode.NOT_PERMISSION);
        }
        return groupService.editGroup(id, username, groupMemberInfo, editGroup) ? R.ok() : R.failed();
    }

    @Override
    public R<List<GroupMemberVO>> getGroupMembers(Long userId, Long groupId) {
        // 判断是否群聊成员
        if (!groupService.isGroupMember(groupId, userId)) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }
        // 查找群聊成员
        List<GroupMemberInfoDTO> groupMembers = iGroupMemberService.queryGroupMembers(groupId);
        if (CollectionUtils.isEmpty(groupMembers)) {
            return R.ok(Collections.emptyList());
        }
        List<GroupMemberVO> vos = groupMembers.stream().map(groupMember -> GroupMemberVO.builder()
                .userId(groupMember.getUserId().toString())
                .role(groupMember.getRole())
                .userInfo(new UserInfoVO(groupMember.getUserId().toString(),
                        StringUtils.isBlank(groupMember.getDisplayName()) ? groupMember.getNickname() : groupMember.getDisplayName(),
                        AccountAvatarUtil.getAvatar(groupMember.getAvatar())))
                .created(DateUtil.formatDateTime(groupMember.getCreated())).build()).toList();
        return R.ok(vos);
    }

    @Override
    public R<Boolean> addGroupMember(Long id, GroupDTO group) {
        Long groupId = group.getGroupId();
        List<Long> userIds = group.getUserIds().stream().distinct().toList();
        // 判断自己是否群聊成员
        GroupMemberDTO groupMemberInfo = iGroupMemberService.getGroupMemberInfo(id, groupId);
        if (groupMemberInfo == null || groupMemberInfo.getDeleted()) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }
        Integer role = groupMemberInfo.getRole();
        if (role.equals(GroupRole.COMMON.role) && !groupMemberInfo.getGroupInvite()) {
            return R.failed(ResultCode.NOT_PERMISSION);
        }
        return groupService.addGroupMembers(groupMemberInfo, userIds) ? R.ok() : R.failed();
    }


    @Override
    public R<Boolean> removeGroupMember(Long id, GroupMemberDTO groupMember) {
        Long groupId = groupMember.getGroupId();
        // 判断是否是群成员，并且是否有权力移除群员
        GroupMemberDTO groupMemberInfo = iGroupMemberService.getGroupMemberInfo(id, groupId);
        if (groupMemberInfo == null || groupMemberInfo.getDeleted()) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }
        if (groupMemberInfo.getRole() == null || !groupMemberInfo.getRole().equals(GroupRole.CREATOR.role)) {
            return R.failed(ResultCode.NOT_PERMISSION);
        }
        GroupMemberDTO member = iGroupMemberService.getGroupMemberInfo(groupMember.getId(), groupId);
        if (member == null) {
            return R.ok();
        }
        return groupService.removeGroupMember(member) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> exitGroup(Long userId, Long groupId) {
        // 判断群聊成员是否存在.
        GroupMemberDTO groupMemberInfo = iGroupMemberService.getGroupMemberInfo(userId, groupId);
        if (groupMemberInfo == null) {
            return R.ok();
        }
        // 如果是群主, 则应该调用解散群接口. 暂不支持群主转移
        if (GroupRole.CREATOR.role.equals(groupMemberInfo.getRole())) {
            return deleteGroup(userId, groupId);
        }
        return groupService.exitGroup(groupMemberInfo) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> deleteGroup(Long userId, Long groupId) {
        // 判断群聊成员是否存在.
        GroupMemberDTO groupMemberInfo = iGroupMemberService.getGroupMemberInfo(userId, groupId);
        if (groupMemberInfo == null) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }
        // 判断是否是群主
        if (!GroupRole.CREATOR.role.equals(groupMemberInfo.getRole())) {
            return R.failed(ResultCode.NOT_PERMISSION);
        }
        return groupService.deleteGroup(groupMemberInfo) ? R.ok() : R.failed();
    }
}
