package com.hqy.cloud.message.controller;

import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import com.hqy.cloud.message.service.request.ImMessageRequestService;
import com.hqy.foundation.common.bind.SocketIoConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/1 13:38
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ImMessageController {
    private final ImMessageRequestService requestService;

    @GetMapping("/im/connection")
    public R<SocketIoConnection> genWsMessageConnection(HttpServletRequest request) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (authentication == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.genWsMessageConnection(request, authentication.getId().toString());
    }








}
