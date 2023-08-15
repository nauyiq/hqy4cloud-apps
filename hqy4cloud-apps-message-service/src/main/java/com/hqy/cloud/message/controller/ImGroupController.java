package com.hqy.cloud.message.controller;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
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
     * @return            R.
     */
    @PostMapping("/group")
    public R<Boolean> createGroup(HttpServletRequest request, @RequestBody GroupDTO createGroup) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (StringUtils.isBlank(createGroup.getName())
                || CollectionUtils.isEmpty(createGroup.getUserIds())
                || createGroup.getUserIds().size() <= 1
                || createGroup.getUserIds().size() >= 499) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.createGroup(id, createGroup);
    }

    /**
     * 修改群聊, 包括公告和群聊名
     * @param request   HttpServletRequest.
     * @param editGroup {@link GroupDTO}
     * @return          R.
     */
    @PutMapping("/group")
    public R<Boolean> editGroup(HttpServletRequest request, @RequestBody GroupDTO editGroup) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (editGroup.getGroupId() == null || StringUtils.isAllBlank(editGroup.getName(), editGroup.getNotice())) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.editGroup(id, editGroup);
    }

    /**
     * 获取群聊成员
     * @param groupId 群聊id
     * @return        R.
     */
    @GetMapping("/group/members/{groupId}")
    public R<List<GroupMemberVO>> getGroupMembers(@PathVariable Long groupId) {
        if (groupId == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.getGroupMembers(groupId);
    }

    /**
     * 添加群聊用户
     * @param request     HttpServletRequest
     * @param groupMember {@link GroupMemberDTO}
     * @return            R.
     */
    @PostMapping("/group/member")
    public R<Boolean> addGroupMember(HttpServletRequest request, @RequestBody GroupMemberDTO groupMember) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (groupMember == null || !groupMember.isEnable()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.addGroupMember(id, groupMember);
    }

    /**
     * 修改群聊成员信息
     * @param request     HttpServletRequest
     * @param groupMember {@link GroupMemberDTO}
     * @return            R.
     */
    @PutMapping("/group/member")
    public R<Boolean> editGroupMembers(HttpServletRequest request, @RequestBody GroupMemberDTO groupMember) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (groupMember == null || !groupMember.isEnable()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
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
     * @return            R.
     */
    @DeleteMapping("/group/member")
    public R<Boolean> removeGroupMember(HttpServletRequest request, @RequestBody GroupMemberDTO groupMember) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (groupMember == null || !groupMember.isEnable()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.removeGroupMember(id, groupMember);
    }







}
