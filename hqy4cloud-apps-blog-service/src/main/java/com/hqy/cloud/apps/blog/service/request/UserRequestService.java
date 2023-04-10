package com.hqy.cloud.apps.blog.service.request;

import com.hqy.cloud.apps.blog.dto.AccountRegistryDTO;
import com.hqy.cloud.apps.blog.dto.BlogUserProfileDTO;
import com.hqy.cloud.apps.blog.dto.ForgetPasswordDTO;
import com.hqy.cloud.apps.blog.vo.AccountProfileVO;
import com.hqy.cloud.common.bind.R;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 17:25
 */
public interface UserRequestService {

    /**
     * get login user info.
     * @param id user id.
     * @return   R.
     */
    R<AccountProfileVO> getLoginUserInfo(Long id);

    /**
     * update login user profile info.
     * @param profile profile data.
     * @return        R.
     */
    R<Boolean> updateLoginUserInfo(BlogUserProfileDTO profile);

    /**
     * 发送邮箱验证码
     * @param email 邮箱
     * @return      R.
     */
    R<Boolean> sendEmailCode(String email);

    /**
     * 发送注册邮箱验证码
     * @param email 邮箱
     * @return      R.
     */
    R<Boolean> sendRegistryEmail(String email);

    /**
     * 注册账号.
     * @param registry AccountRegistryDTO.
     * @return         R.
     */
    R<Boolean> registryAccount(AccountRegistryDTO registry);


    /**
     * 重设密码
     * @param passwordDTO {@link ForgetPasswordDTO}.
     * @return            R.
     */
    R<Boolean> resetPassword(ForgetPasswordDTO passwordDTO);

    /**
     * 修改密码
     * @param accountId   账号id
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return            R.
     */
    R<Boolean> updatePassword(Long accountId, String oldPassword, String newPassword);



}
