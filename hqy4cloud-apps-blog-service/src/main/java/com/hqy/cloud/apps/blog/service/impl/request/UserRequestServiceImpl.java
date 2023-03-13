package com.hqy.cloud.apps.blog.service.impl.request;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.service.RemoteAccountProfileService;
import com.hqy.account.service.RemoteAccountService;
import com.hqy.account.struct.AccountProfileStruct;
import com.hqy.account.struct.AccountStruct;
import com.hqy.account.struct.RegistryAccountStruct;
import com.hqy.cloud.apps.blog.dto.AccountBaseRegistryDTO;
import com.hqy.cloud.apps.blog.dto.AccountRegistryDTO;
import com.hqy.cloud.apps.blog.dto.BlogUserProfileDTO;
import com.hqy.cloud.apps.blog.dto.ForgetPasswordDTO;
import com.hqy.cloud.apps.blog.service.request.UserRequestService;
import com.hqy.cloud.apps.blog.vo.AccountProfileVO;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.foundation.common.account.AccountRandomCodeServer;
import com.hqy.cloud.foundation.common.account.AccountRegistryAccountRandomCodeServer;
import com.hqy.cloud.service.EmailRemoteService;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.ValidationUtil;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import com.hqy.rpc.thrift.struct.CommonResultStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static com.hqy.cloud.common.result.ResultCode.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 17:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRequestServiceImpl implements UserRequestService {

    private final AccountRandomCodeServer randomCodeServer = new AccountRegistryAccountRandomCodeServer();

    @Override
    public R<AccountProfileVO> getLoginUserInfo(Long id) {
        RemoteAccountService remoteService = RPCClient.getRemoteService(RemoteAccountService.class);
        String accountInfoJson = remoteService.getAccountInfoJson(id);
        if (StringUtils.isBlank(accountInfoJson)) {
            return R.failed(USER_NOT_FOUND);
        }

        AccountInfoDTO accountInfoDTO = JsonUtil.toBean(accountInfoJson, AccountInfoDTO.class);
        return R.ok(new AccountProfileVO(accountInfoDTO.getId().toString(), accountInfoDTO.getUsername(), accountInfoDTO.getNickname(), accountInfoDTO.getAvatar(), accountInfoDTO.getIntro(), accountInfoDTO.getBirthday()));
    }


    @Override
    public R<Boolean> updateLoginUserInfo(BlogUserProfileDTO profile) {
        String avatar = profile.getAvatar();
        if (StringUtils.isNotBlank(avatar)) {
            if (avatar.startsWith(StringConstants.HTTP)) {
                avatar = avatar.substring(avatar.indexOf("/files"));
            }
        }

        RemoteAccountProfileService remoteService = RPCClient.getRemoteService(RemoteAccountProfileService.class);
        boolean update = remoteService.uploadAccountProfile(new AccountProfileStruct(profile.getId(), profile.getNickname(), avatar, profile.getIntro(), profile.getBirthday()));
        if (!update) {
            return R.failed();
        }
        return R.ok();
    }

    @Override
    public R<Boolean> sendRegistryEmail(AccountBaseRegistryDTO registry) {
        String email = registry.getEmail();
        if (!ValidationUtil.validateEmail(email)) {
            return R.failed(INVALID_EMAIL);
        }
        //check registry info.
        RemoteAccountService service = RPCClient.getRemoteService(RemoteAccountService.class);
        CommonResultStruct result = service.checkRegistryInfo(registry.getUsername(), email);
        if (!result.result) {
            return R.failed(result.message, result.code);
        }
        //get email code.
        String code = randomCodeServer.randomCode(registry.getUsername(), email, 6);
        //send email.
        EmailRemoteService emailRemoteService = RPCClient.getRemoteService(EmailRemoteService.class);
        emailRemoteService.sendRegistryEmail(email, registry.getUsername(), code);

        return R.ok();
    }

    @Override
    public R<Boolean> registryAccount(AccountRegistryDTO registry) {
        //校验邮箱验证码
        if (!randomCodeServer.isExist(registry.getUsername(), registry.getEmail(), registry.getCode())) {
            return R.failed(VERIFY_CODE_ERROR);
        }
        //RPC注册账号
        RemoteAccountService accountRemoteService = RPCClient.getRemoteService(RemoteAccountService.class);
        CommonResultStruct commonResultStruct = accountRemoteService.registryAccount(new RegistryAccountStruct(registry.getUsername(), registry.getEmail(), registry.getPassword()));
        if (!commonResultStruct.isResult()) {
            return R.failed();
        }
        return R.ok();
    }

    @Override
    public R<Boolean> sendForgetPasswordEmail(String usernameOrEmail) {
        //校验用户名或者邮箱是否存在
        RemoteAccountService service = RPCClient.getRemoteService(RemoteAccountService.class);
        AccountStruct struct = service.getAccountStructByUsernameOrEmail(usernameOrEmail);
        if (struct == null || struct.id == null) {
            return R.failed(USER_NOT_FOUND);
        }
        //生成邮箱校验码
        String code = randomCodeServer.randomCode(usernameOrEmail, usernameOrEmail, 6);
        //send email.
        EmailRemoteService emailRemoteService = RPCClient.getRemoteService(EmailRemoteService.class);
        emailRemoteService.sendRegistryEmail(struct.email, struct.getUsername(), code);
        return R.ok();
    }

    @Override
    public R<Boolean> resetPassword(ForgetPasswordDTO passwordDTO) {
        String usernameOrEmail = passwordDTO.getUsernameOrEmail();
        //校验邮箱验证码是否准确
        if (!randomCodeServer.isExist(usernameOrEmail, usernameOrEmail, passwordDTO.getCode())) {
            return R.failed(VERIFY_CODE_ERROR);
        }
        //修改用户密码
        RemoteAccountService remoteService = RPCClient.getRemoteService(RemoteAccountService.class);
        CommonResultStruct commonResultStruct = remoteService.updateAccountPassword(usernameOrEmail, passwordDTO.getPassword());
        if (!commonResultStruct.isResult()) {
            return R.failed(commonResultStruct.message, commonResultStruct.code);
        }
        return R.ok();
    }

    @Override
    public R<Boolean> updatePassword(Long accountId, String oldPassword, String newPassword) {
        RemoteAccountService remoteService = RPCClient.getRemoteService(RemoteAccountService.class);
        CommonResultStruct commonResultStruct = remoteService.updateAccountPasswordByIdAndOldPassword(accountId, oldPassword, newPassword);
        if (!commonResultStruct.isResult()) {
            return R.failed(commonResultStruct.message, commonResultStruct.code);
        }
        return R.ok();
    }
}
