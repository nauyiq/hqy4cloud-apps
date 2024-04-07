package com.hqy.cloud.apps.blog.service.opeations;

import com.hqy.cloud.apps.blog.dto.AccountRegistryDTO;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/20
 */
public interface UserInfoOperationsService {

    /**
     * 注册账号
     * @param  account 注册账号信息
     * @return         是否注册成功
     */
    boolean registryAccount(AccountRegistryDTO account);

}
