package com.hqy.blog.controller;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.dto.BlogUserProfileDTO;
import com.hqy.blog.service.request.UserRequestService;
import com.hqy.util.AssertUtil;
import com.hqy.util.OauthRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * UserController.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/26 17:45
 */
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final UserRequestService userRequestService;

    /**
     * 获取登录用户信息.
     * @param request request.
     * @return        DataResponse.
     */
    @GetMapping("/admin/account")
    public DataResponse getLoginUserInfo(HttpServletRequest request) {
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        AssertUtil.notNull(id, CommonResultCode.SYSTEM_BUSY.message);
        return userRequestService.getLoginUserInfo(id);
    }

    @PutMapping("/admin/account")
    public DataResponse updateLoginUserInfo(@RequestBody BlogUserProfileDTO profile, HttpServletRequest request) {
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        profile.setId(id);
        return userRequestService.updateLoginUserInfo(profile);
    }




}
