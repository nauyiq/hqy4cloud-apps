package com.hqy.cloud.message.controller;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.dto.FriendDTO;
import com.hqy.cloud.message.bind.vo.FriendApplicationVO;
import com.hqy.cloud.message.service.request.ImFriendRequestService;
import com.hqy.cloud.web.common.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 聊天好友API 控制器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/im/friend")
public class ImFriendController extends BaseController {
    private final ImFriendRequestService requestService;

    /**
     * 获取好友申请列表
     * @param request HttpServletRequest.
     * @return        R
     */
    @GetMapping("/applications")
    public R<List<FriendApplicationVO>> getFriendApplications(HttpServletRequest request) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getFriendApplications(accountId);
    }

    /**
     * 申请添加好友
     * @param request       HttpServletRequest.
     * @param addFriendData 请求参数
     * @return              R.
     */
    @PostMapping("/application")
    public R<Boolean> applyAddFriend(HttpServletRequest request, @RequestBody @Valid FriendDTO addFriendData) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (addFriendData == null || addFriendData.getUserId() == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.applyAddFriend(accountId, addFriendData.getUserId(), addFriendData.getRemark());
    }

    /**
     * 接收或者拒绝好友申请
     * @param request   HttpServletRequest.
     * @param friendDTO 请求参数
     * @return          R.
     */
    @PutMapping("/application")
    public R<Boolean> acceptOrRejectFriendApplication(HttpServletRequest request, @RequestBody FriendDTO friendDTO) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (friendDTO == null || friendDTO.getApplicationId() == null || friendDTO.getStatus() == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.acceptOrRejectFriendApplication(accountId, friendDTO.getApplicationId(), friendDTO.getStatus());
    }


    /**
     * 修改好友的信息, 目前就是修改好友备注.
     * @param request   HttpServletRequest.
     * @param friend    入参
     * @return          R.
     */
    @PutMapping
    public R<Boolean> updateFriendInfo(HttpServletRequest request, @RequestBody FriendDTO friend) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (friend == null || friend.getUserId() == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.updateFriendInfo(accountId, friend);
    }


    @DeleteMapping("/{userId}")
    public R<Boolean> removeFriend(HttpServletRequest request, @PathVariable("userId") Long userId) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.removeFriend(accountId, userId);
    }



    /**
     * 获取当前登录用户的所有好友
     * @param request HttpServletRequest.
     * @return        R
     */
    /*@GetMapping("/friends")
    public R<List<IndexFriendsVO>> getFriends(HttpServletRequest request) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getImFriends(accountId);
    }*/





}
