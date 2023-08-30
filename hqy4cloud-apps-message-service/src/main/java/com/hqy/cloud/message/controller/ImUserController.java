package com.hqy.cloud.message.controller;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.dto.FriendDTO;
import com.hqy.cloud.message.bind.vo.ContactVO;
import com.hqy.cloud.message.bind.vo.FriendVO;
import com.hqy.cloud.message.bind.vo.UserImSettingVO;
import com.hqy.cloud.message.service.request.ImUserRequestService;
import com.hqy.cloud.web.common.BaseController;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 聊天用户相关接口API
 * @author qiyuan.hong
 * @date 2023-08-12 11:55
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/im/user")
public class ImUserController extends BaseController {
    private final ImUserRequestService requestService;

    /**
     * 获取用户聊天设置
     * @param request HttpServletRequest.
     * @return        R.
     */
    @GetMapping("/setting")
    public R<UserImSettingVO> getUserImSetting(HttpServletRequest request) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getUserImSetting(accountId);
    }

    /**
     * 获取通讯录列表
     * @param request HttpServletRequest.
     * @return        R.
     */
    @GetMapping("/contacts")
    public R<List<ContactVO>> getUserImContacts(HttpServletRequest request) {
        Long userId = getAccessAccountId(request);
        if (userId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getUserImContacts(userId);
    }

    /**
     * 获取当前im用户所有好友列表
     * @param request HttpServletRequest.
     * @return        R.
     */
    @GetMapping("/friends")
    public R<List<FriendVO>> getFriends(HttpServletRequest request) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getImFriends(id);
    }

    /**
     * 申请添加好友
     * @param request   HttpServletRequest.
     * @param add       {@link FriendDTO}
     * @return          R.
     */
    @PostMapping("/friend")
    public R<Boolean> addImFriend(HttpServletRequest request, @RequestBody FriendDTO add) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (add == null || (add.getUserId() == null)) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.addImFriend(id, add);
    }

    /**
     * 同意或者拒绝添加好友申请
     * @param request   HttpServletRequest.
     * @param friendDTO {@link FriendDTO}
     * @return          R.
     */
    @PutMapping("/friend")
    public R<Boolean> acceptOrRejectImFriend(HttpServletRequest request,  @RequestBody FriendDTO friendDTO) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (friendDTO == null || friendDTO.getUserId() == null || friendDTO.getStatus() == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.acceptOrRejectImFriend(id, friendDTO);
    }

    /**
     * 修改好友备注
     * @param request   HttpServletRequest.
     * @param friendDTO {@link FriendDTO}
     * @return          R.
     */
    @PutMapping("/friend/mark")
    public R<Boolean> updateFriendMark(HttpServletRequest request, @RequestBody FriendDTO friendDTO) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (friendDTO == null || friendDTO.getUserId() == null || StringUtils.isBlank(friendDTO.getRemark())) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.updateFriendMark(id, friendDTO.getUserId(), friendDTO.getRemark());
    }

    /**
     * 移除好友
     * @param request HttpServletRequest.
     * @param userId  好友id
     * @return        R.
     */
    @DeleteMapping("/friend/{userId}")
    public R<Boolean> removeFriend(HttpServletRequest request, @PathVariable("userId") Long userId) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (userId == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.removeFriend(id, userId);
    }










}
