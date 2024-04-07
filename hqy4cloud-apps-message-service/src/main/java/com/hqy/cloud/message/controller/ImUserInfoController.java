package com.hqy.cloud.message.controller;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.dto.ImUserSettingInfoDTO;
import com.hqy.cloud.message.bind.vo.UserCardVO;
import com.hqy.cloud.message.bind.vo.UserImSettingVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.service.request.ImUserSettingRequestService;
import com.hqy.cloud.web.common.BaseController;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 聊天用户信息相关API 控制器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/im/user")
public class ImUserInfoController extends BaseController {
    private final ImUserSettingRequestService requestService;

    /**
     * 获取用户聊天设置
     * @param request HttpServletRequest.
     * @return        R.
     */
    @GetMapping("/setting")
    public R<UserImSettingVO> getUserImSettingInfo(HttpServletRequest request) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getUserImSettingInfo(accountId);
    }

    /**
     * 更新用户聊天设置
     * @param request HttpServletRequest.
     * @param setting 用户聊天设置.
     * @return        R.
     */
    @PutMapping("/setting")
    public R<Boolean> updateUserImSettingInfo(HttpServletRequest request, @Valid @RequestBody UserImSettingVO setting) {
        if (setting == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        String nickname = setting.getNickname();
        if (StringUtil.isNotBlank(nickname) && SensitiveWordHelper.contains(nickname)) {
            return R.failed(ResultCode.INVALID_DATA);
        }
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.updateUserImSettingInfo(accountId, setting);
    }


    /**
     * 修改用户聊天信息
     * @param request  HttpServletRequest.
     * @param userInfo 用户信息
     * @return         是否修改成功
     */
    @PutMapping
    public R<Boolean> updateImUserInfo(HttpServletRequest request, @RequestBody @Valid ImUserSettingInfoDTO userInfo) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.updateImUserInfo(accountId, userInfo);
    }



    /**
     * 查询用户的聊天设置
     * @param request HttpServletRequest.
     * @param userId  用户id
     * @return        R.
     */
    @GetMapping("/{userId}")
    public R<UserCardVO> getImUserCardInfo(HttpServletRequest request, @PathVariable("userId") Long userId) {
        if (userId == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        Long id = getAccessAccountId(request);
        return requestService.getImUserInfo(id, userId);
    }

    /**
     * 根据用户名或昵称查询用户
     * @param request HttpServletRequest.
     * @param name    输入的名字
     * @return        R.
     */
    @GetMapping("/search")
    public R<List<UserInfoVO>> searchImUsers(HttpServletRequest request, String name) {
        if (StringUtils.isEmpty(name)) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.searchImUsers(accountId, name);
    }




}
