package com.hqy.blog.service.impl;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.service.remote.AccountRemoteService;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.service.AdminUserRequestService;
import com.hqy.blog.vo.AccountProfileVO;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 17:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserRequestServiceImpl implements AdminUserRequestService {

    @Override
    public DataResponse getLoginUserInfo(Long id) {
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        String accountInfoJson = accountRemoteService.getAccountInfoJson(id);
        AssertUtil.notEmpty(accountInfoJson, CommonResultCode.USER_NOT_FOUND.message);

        AccountInfoDTO accountInfoDTO = JsonUtil.toBean(accountInfoJson, AccountInfoDTO.class);
        return CommonResultCode.dataResponse(CommonResultCode.SUCCESS,
                new AccountProfileVO(accountInfoDTO.getId(), accountInfoDTO.getUsername(), accountInfoDTO.getNickname(), accountInfoDTO.getAvatar(), accountInfoDTO.getIntro(), accountInfoDTO.getBirthday()));
    }
}
