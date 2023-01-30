package com.hqy.blog.controller;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.dto.AccountBaseRegistryDTO;
import com.hqy.blog.dto.AccountRegistryDTO;
import com.hqy.blog.dto.BlogUserProfileDTO;
import com.hqy.blog.dto.ForgetPasswordDTO;
import com.hqy.blog.service.request.UserRequestService;
import com.hqy.util.AssertUtil;
import com.hqy.util.OauthRequestUtil;
import com.hqy.web.global.BaseController;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

    @PostMapping("/account/registry/email")
    public MessageResponse sendRegistryEmail(@RequestBody @Valid AccountBaseRegistryDTO registry) {
        AssertUtil.notNull(registry, "Registry data should not be null.");
        return userRequestService.sendRegistryEmail(registry);
    }

    @PostMapping("/account/password/email")
    public MessageResponse sendForgetPasswordEmail(@RequestParam("usernameOrEmail") String usernameOrEmail) {
        if (StringUtils.isBlank(usernameOrEmail)) {
            return CommonResultCode.messageResponse(CommonResultCode.ERROR_PARAM.code, "username or email should not be empty.");
        }
        return userRequestService.sendForgetPasswordEmail(usernameOrEmail);
    }

    @PostMapping("/account/password/reset")
    public MessageResponse resetPassword(@RequestBody @Valid ForgetPasswordDTO passwordDTO) {
        AssertUtil.notNull(passwordDTO, "Reset password data should not be null.");
        return userRequestService.resetPassword(passwordDTO);
    }

    @PutMapping("/account/password")
    public MessageResponse updatePassword(HttpServletRequest request, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        if (StringUtils.isAnyBlank(oldPassword, newPassword)) {
            return CommonResultCode.messageResponse(CommonResultCode.ERROR_PARAM);
        }
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return CommonResultCode.messageResponse(CommonResultCode.USER_NOT_FOUND);
        }
        return userRequestService.updatePassword(accountId, oldPassword, newPassword);
    }


    @PostMapping("/account/registry")
    public MessageResponse registryAccount(@RequestBody @Valid AccountRegistryDTO registry) {
        AssertUtil.notNull(registry, "Registry data should not be null.");
        return userRequestService.registryAccount(registry);
    }

    /**
     * 获取登录用户信息.
     * @param request request.
     * @return        DataResponse.
     */
    @GetMapping("/account")
    public DataResponse getLoginUserInfo(HttpServletRequest request) {
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        AssertUtil.notNull(id, CommonResultCode.USER_NOT_FOUND.message);
        return userRequestService.getLoginUserInfo(id);
    }

    @PutMapping("/account/profile")
    public DataResponse updateLoginUserInfo(@RequestBody BlogUserProfileDTO profile, HttpServletRequest request) {
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        profile.setId(id);
        return userRequestService.updateLoginUserInfo(profile);
    }






}
