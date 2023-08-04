package com.hqy.cloud.apps.blog.controller;

import com.hqy.cloud.apps.blog.service.request.ConfigRequestService;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import com.hqy.foundation.common.bind.SocketIoConnection;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/11/4 13:29
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog")
public class ConfigController {
    private final ConfigRequestService configRequestService;

    @GetMapping("/aboutMe")
    public R<String> getAboutMe() {
        return configRequestService.getAboutMe();
    }

    @GetMapping("/connection")
    public R<SocketIoConnection> genWsMessageConnection(HttpServletRequest request) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (authentication == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return configRequestService.genWsBlogConnection(request, authentication.getId().toString());
    }



}
