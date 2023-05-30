package com.hqy.cloud.apps.blog.controller;

import cn.hutool.core.lang.Validator;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.hqy.cloud.apps.blog.dto.AccountRegistryDTO;
import com.hqy.cloud.apps.blog.dto.BlogUserProfileDTO;
import com.hqy.cloud.apps.blog.dto.ForgetPasswordDTO;
import com.hqy.cloud.apps.blog.service.request.UserRequestService;
import com.hqy.cloud.apps.blog.vo.AccountProfileVO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.web.common.BaseController;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

/**
 * AccountController.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/26 17:45
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog")
public class AccountController extends BaseController {

    private final UserRequestService userRequestService;

    @PostMapping("/email/{email}")
    @SentinelResource(value = "sendEmailCode")
    public R<Boolean> sendEmailCode(@PathVariable("email") String email) {
        return userRequestService.sendEmailCode(email);
    }

    @PostMapping("/email/registry/{email}")
    @SentinelResource(value = "sendRegistryEmail")
    public R<Boolean> sendRegistryEmail(@PathVariable("email") String email) {
        if (!Validator.isEmail(email)) {
            return R.failed(ResultCode.INVALID_EMAIL);
        }
        return userRequestService.sendRegistryEmail(email);
    }


    @PostMapping("/account/password/forget")
    public R<Boolean> resetPassword(@RequestBody @Valid ForgetPasswordDTO passwordDTO) {
        AssertUtil.notNull(passwordDTO, "Reset password data should not be null.");
        return userRequestService.resetPassword(passwordDTO);
    }

    @PutMapping("/account/password")
    public R<Boolean> updatePassword(HttpServletRequest request, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        if (StringUtils.isAnyBlank(oldPassword, newPassword)) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        Long accountId = getAccessAccountId(request);
        if (Objects.isNull(accountId)) {
            return R.failed(ResultCode.USER_NOT_FOUND);
        }
        return userRequestService.updatePassword(accountId, oldPassword, newPassword);
    }


    @PostMapping("/account/registry")
    public R<Boolean> registryAccount(@RequestBody @Valid AccountRegistryDTO registry) {
        AssertUtil.notNull(registry, "Registry data should not be null.");
        return userRequestService.registryAccount(registry);
    }

    /**
     * 获取登录用户信息.
     * @param request request.
     * @return        DataResponse.
     */
    @GetMapping("/account")
    public R<AccountProfileVO> getLoginUserInfo(HttpServletRequest request) {
        Long accountId = getAccessAccountId(request);
        return userRequestService.getLoginUserInfo(accountId);
    }

    @PutMapping("/account/profile")
    public R<Boolean> updateLoginUserInfo(@RequestBody BlogUserProfileDTO profile, HttpServletRequest request) {
        Long accountId = getAccessAccountId(request);
        if(Objects.isNull(accountId)) {
            return R.failed(ResultCode.USER_NOT_FOUND);
        }
        profile.setId(accountId);
        return userRequestService.updateLoginUserInfo(profile);
    }






}
