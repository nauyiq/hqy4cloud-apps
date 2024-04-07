package com.hqy.cloud.apps.blog.service.opeations.impl;

import com.hqy.cloud.account.service.RemoteAccountService;
import com.hqy.cloud.account.struct.RegistryAccountStruct;
import com.hqy.cloud.apps.blog.dto.AccountRegistryDTO;
import com.hqy.cloud.apps.blog.service.opeations.UserInfoOperationsService;
import com.hqy.cloud.foundation.common.account.AvatarHostUtil;
import com.hqy.cloud.message.service.UserSettingThriftService;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.cloud.rpc.thrift.struct.CommonResultStruct;
import com.hqy.cloud.util.AssertUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoOperationsServiceImpl implements UserInfoOperationsService {

    @Override
    @GlobalTransactional(rollbackFor = Exception.class, name = "registryAccount")
    public boolean registryAccount(AccountRegistryDTO account) {
        // 调用账号RPC进行主账号表的账号注册
        RemoteAccountService accountRemoteService = RpcClient.getRemoteService(RemoteAccountService.class);
        RegistryAccountStruct accountStruct = new RegistryAccountStruct(account.getUsername(), account.getEmail(), account.getPassword());
        CommonResultStruct resultStruct = accountRemoteService.tccRegistryAccount(accountStruct);
        AssertUtil.isTrue(resultStruct.result, resultStruct.message);
        Long id = Long.parseLong(resultStruct.message);
        // 调用聊天rpc注册聊天账号
        UserSettingThriftService userSettingThriftService = RpcClient.getRemoteService(UserSettingThriftService.class);
        boolean addImUser = userSettingThriftService.addImUser(id, account.getUsername(), account.getUsername(), AvatarHostUtil.settingAvatar(AvatarHostUtil.DEFAULT_AVATAR));
        AssertUtil.isTrue(addImUser, "Failed execute to add im user");
        return true;
    }
}
