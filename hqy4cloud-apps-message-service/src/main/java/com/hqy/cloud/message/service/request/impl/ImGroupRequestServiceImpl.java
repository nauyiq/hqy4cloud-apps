package com.hqy.cloud.message.service.request.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.enums.GroupRole;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.bind.vo.GroupMemberVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.service.ImGroupOperationsService;
import com.hqy.cloud.message.service.request.ImGroupRequestService;
import com.hqy.cloud.message.tk.entity.ImGroup;
import com.hqy.cloud.message.tk.entity.ImGroupMember;
import com.hqy.cloud.message.tk.service.ImGroupMemberTkService;
import com.hqy.cloud.message.tk.service.ImGroupTkService;
import com.hqy.cloud.web.common.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.result.AppsResultCode.IM_GROUP_EXIST;
import static com.hqy.cloud.apps.commom.result.AppsResultCode.IM_GROUP_NOT_EXIST;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:38
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImGroupRequestServiceImpl implements ImGroupRequestService {
    private final ImGroupTkService groupTkService;
    private final ImGroupMemberTkService groupMemberTkService;
    private final ImGroupOperationsService groupOperationsService;

    @Override
    public R<Boolean> createGroup(Long id, GroupDTO createGroup) {
        //判断当前群聊是否存在. 同一个用户创建的群聊名称不能一致.
        ImGroup group = ImGroup.of(createGroup.getName(), id);
        group = groupTkService.queryOne(group);
        if (group != null) {
            return R.failed(IM_GROUP_EXIST);
        }
        return groupOperationsService.createGroup(id, createGroup) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> editGroup(Long id, GroupDTO editGroup) {
        GroupMemberDTO groupMemberInfo = groupTkService.getGroupMemberInfo(id, editGroup.getGroupId());
        if (groupMemberInfo == null) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }
        if (groupMemberInfo.getRole() == null || groupMemberInfo.getRole().equals(GroupRole.COMMON.role)) {
            return R.failed(ResultCode.NOT_PERMISSION);
        }
        return groupOperationsService.editGroup(groupMemberInfo, editGroup) ? R.ok() : R.failed();
    }

    @Override
    public R<List<GroupMemberVO>> getGroupMembers(Long groupId) {
        ImGroup group = groupTkService.queryById(groupId);
        if (group == null) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }
        List<ImGroupMember> groupMembers = groupMemberTkService.queryList(ImGroupMember.of(groupId, true));
        if (CollectionUtils.isEmpty(groupMembers)) {
            return R.ok(Collections.emptyList());
        }
        List<Long> userIds = groupMembers.stream().map(ImGroupMember::getUserId).toList();
        Map<Long, AccountBaseInfoStruct> infos = AccountRpcUtil.getAccountBaseInfoMap(userIds);
        if (MapUtil.isEmpty(infos)) {
            return R.ok(Collections.emptyList());
        }
        return R.ok(convertGroupMembers(groupMembers, infos));
    }

    private List<GroupMemberVO> convertGroupMembers(List<ImGroupMember> groupMembers, Map<Long, AccountBaseInfoStruct> infos) {
        return groupMembers.parallelStream().map(member -> {
            Long userId = member.getUserId();
            String displayName = member.getDisplayName();
            if (userId == null || !infos.containsKey(userId)) {
                return null;
            }
            GroupMemberVO vo = new GroupMemberVO(member.getUserId().toString(), member.getRole(), DateUtil.formatDateTime(member.getCreated()));
            AccountBaseInfoStruct infoStruct = infos.get(userId);
            UserInfoVO userInfoVO = new UserInfoVO(userId.toString(), infoStruct.username, infoStruct.nickname, infoStruct.avatar,
                    StringUtils.isEmpty(displayName) ? StrUtil.EMPTY : displayName);
            vo.setUserInfo(userInfoVO);
            return vo;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public R<Boolean> addGroupMember(Long id, GroupMemberDTO groupMember) {
        GroupMemberDTO groupMemberInfo = groupTkService.getGroupMemberInfo(id, groupMember.getGroupId());
        if (groupMemberInfo == null) {
            return R.failed(IM_GROUP_NOT_EXIST);
        }
        Integer role = groupMemberInfo.getRole();
        if (role == null || !role.equals(GroupRole.CREATOR.role)) {
            return R.failed(ResultCode.NOT_PERMISSION);
        }
        return groupOperationsService.addGroupMember(groupMember) ? R.ok() : R.failed();
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
        if (!id.equals(groupMember.getId()) || !role.equals(GroupRole.CREATOR.role)) {
            return R.failed(ResultCode.NOT_PERMISSION);
        }
        return groupOperationsService.removeGroupMember(groupMember.getId(), groupMember.getGroupId()) ? R.ok() : R.failed();
    }
}
