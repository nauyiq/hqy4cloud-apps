package com.hqy.cloud.message.controller;

import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.bind.vo.GroupMemberVO;
import com.hqy.cloud.message.service.request.ImGroupRequestService;
import com.hqy.cloud.web.common.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.hqy.cloud.common.result.ResultCode.ERROR_PARAM_UNDEFINED;
import static com.hqy.cloud.common.result.ResultCode.NOT_LOGIN;
import static com.hqy.cloud.message.tk.entity.ImGroupMember.MAX_MEMBERS;

/**
 * 群聊相关API
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:33
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/im")
public class ImGroupController extends BaseController {
    private final ImGroupRequestService requestService;

    /**
     * 新建群聊
     * @param request     HttpServletRequest
     * @param createGroup {@link GroupDTO}
     * @return R.
     */
    @PostMapping("/group")
    public R<Boolean> createGroup(HttpServletRequest request, @RequestBody GroupDTO createGroup) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        List<Long> userIds = createGroup.getUserIds();
        // 创建的群聊人数不能低于2人
        // 群聊人数已达到极限
        if (CollectionUtils.isEmpty(userIds) || userIds.size() <= 1) {
            return R.failed(ERROR_PARAM_UNDEFINED);
        }
        if (userIds.size() >= MAX_MEMBERS - 1) {
            return R.failed(AppsResultCode.IM_GROUP_MEMBER_COUNT_LIMITED);
        }
        return requestService.createGroup(accountId, createGroup);
    }

    /**
     * 修改群聊, 包括公告和群聊名
     * @param request   HttpServletRequest.
     * @param editGroup {@link GroupDTO}
     * @return R.
     */
    @PutMapping("/group")
    public R<Boolean> editGroup(HttpServletRequest request, @RequestBody GroupDTO editGroup) {
        AuthenticationInfo info = AuthenticationRequestContext.getAuthentication(request);
        if (info == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (editGroup.getGroupId() == null || StringUtils.isAllBlank(editGroup.getName(), editGroup.getNotice())) {
            return R.failed(ERROR_PARAM_UNDEFINED);
        }
        return requestService.editGroup(info, editGroup);
    }

    /**
     * 获取群聊成员
     * @param request HttpServletRequest.
     * @param groupId 群聊id
     * @return R.
     */
    @GetMapping("/group/members/{groupId}")
    public R<List<GroupMemberVO>> getGroupMembers(HttpServletRequest request, @PathVariable Long groupId) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(NOT_LOGIN);
        }
        if (groupId == null) {
            return R.failed(ERROR_PARAM_UNDEFINED);
        }
        return requestService.getGroupMembers(id, groupId);
    }

    /**
     * 添加群聊用户
     * @param request     HttpServletRequest
     * @param group      {@link GroupDTO}
     * @return R.
     */
    @PostMapping("/group/member")
    public R<Boolean> addGroupMember(HttpServletRequest request, @RequestBody GroupDTO group) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (group == null || group.getGroupId() == null || CollectionUtils.isEmpty(group.getUserIds())) {
            return R.failed(ERROR_PARAM_UNDEFINED);
        }
        return requestService.addGroupMember(id, group);
    }

    /**
     * 修改群聊成员信息
     * @param request     HttpServletRequest
     * @param groupMember {@link GroupMemberDTO}
     * @return R.
     */
    @PutMapping("/group/member")
    public R<Boolean> editGroupMembers(HttpServletRequest request, @RequestBody GroupMemberDTO groupMember) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (groupMember == null || !groupMember.isEnable()) {
            return R.failed(ERROR_PARAM_UNDEFINED);
        }
        if (StringUtils.isBlank(groupMember.getDisplayName()) || groupMember.getRole() == null) {
            return R.ok();
        }
        return requestService.editGroupMember(id, groupMember);
    }

    /**
     * 移除群聊用户
     * @param request     HttpServletRequest
     * @param groupMember {@link GroupMemberDTO}
     * @return R.
     */
    @DeleteMapping("/group/member")
    public R<Boolean> removeGroupMember(HttpServletRequest request, @RequestBody GroupMemberDTO groupMember) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (groupMember == null || !groupMember.isEnable()) {
            return R.failed(ERROR_PARAM_UNDEFINED);
        }
        return requestService.removeGroupMember(id, groupMember);
    }

    /**
     * 退出群聊
     * @param request HttpServletRequest
     * @param groupId 所在群id
     * @return        R.
     */
    @DeleteMapping("/group/exit/{groupId}")
    public R<Boolean> exitGroup(HttpServletRequest request, @PathVariable("groupId") Long groupId) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.exitGroup(id, groupId);
    }

    /**
     * 删除/解散 群聊
     * @param request HttpServletRequest
     * @param groupId 所在群id
     * @return        R.
     */
    @DeleteMapping("/group/{groupId}")
    public R<Boolean> deleteGroup(HttpServletRequest request, @PathVariable("groupId") Long groupId) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.deleteGroup(id, groupId);
    }


}
