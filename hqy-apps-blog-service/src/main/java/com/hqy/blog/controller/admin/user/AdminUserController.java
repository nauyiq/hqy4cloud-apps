package com.hqy.blog.controller.admin.user;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.service.AdminUserRequestService;
import com.hqy.util.AssertUtil;
import com.hqy.util.OauthRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * AdminUserController.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/26 17:45
 */
@RestController
@RequestMapping(("/user"))
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserRequestService adminUserRequestService;

    /**
     * 获取登录用户信息.
     * @param request request.
     * @return        DataResponse.
     */
    @GetMapping
    public DataResponse getLoginUserInfo(HttpServletRequest request) {
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        AssertUtil.notNull(id, CommonResultCode.SYSTEM_BUSY.message);
        return adminUserRequestService.getLoginUserInfo(id);
    }




}
