package com.hqy.blog.service.request;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.blog.dto.AccountBaseRegistryDTO;
import com.hqy.blog.dto.AccountRegistryDTO;
import com.hqy.blog.dto.BlogUserProfileDTO;
import com.hqy.blog.dto.ForgetPasswordDTO;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 17:25
 */
public interface UserRequestService {

    /**
     * get login user info.
     * @param id user id.
     * @return   DataResponse.
     */
    DataResponse getLoginUserInfo(Long id);

    /**
     * update login user profile info.
     * @param profile profile data.
     * @return        DataResponse.
     */
    DataResponse updateLoginUserInfo(BlogUserProfileDTO profile);

    /**
     * 发送注册邮件
     * @param registry AccountBaseRegistryDTO.
     * @return         MessageResponse.
     */
    MessageResponse sendRegistryEmail(AccountBaseRegistryDTO registry);

    /**
     * 注册账号.
     * @param registry AccountRegistryDTO.
     * @return         MessageResponse.
     */
    MessageResponse registryAccount(AccountRegistryDTO registry);

    /**
     * 发送忘记密码邮件
     * @param usernameOrEmail 用户名或者邮箱
     * @return                MessageResponse
     */
    MessageResponse sendForgetPasswordEmail(String usernameOrEmail);

    /**
     * 重设密码
     * @param passwordDTO {@link ForgetPasswordDTO}.
     * @return            MessageResponse.
     */
    MessageResponse resetPassword(ForgetPasswordDTO passwordDTO);

    /**
     * 修改密码
     * @param accountId   账号id
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return
     */
    MessageResponse updatePassword(Long accountId, String oldPassword, String newPassword);

}
