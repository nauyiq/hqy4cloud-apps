package com.hqy.cloud.message.controller;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import com.hqy.cloud.message.bind.vo.UserImSettingVO;
import com.hqy.cloud.message.service.request.ImUserRequestService;
import com.hqy.cloud.web.common.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 聊天用户相关接口API
 * @author qiyuan.hong
 * @date 2023-08-12 11:55
 */
@RestController
@RequestMapping("/im")
@RequiredArgsConstructor
public class ImUserController extends BaseController {
    private final ImUserRequestService requestService;

    @GetMapping("/setting")
    public R<UserImSettingVO> getUserImSetting(HttpServletRequest request) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getUserImSetting(accountId);
    }



}
