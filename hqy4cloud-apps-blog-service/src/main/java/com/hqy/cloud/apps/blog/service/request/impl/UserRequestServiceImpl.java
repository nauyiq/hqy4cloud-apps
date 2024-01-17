package com.hqy.cloud.apps.blog.service.request.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.account.service.RemoteAccountProfileService;
import com.hqy.cloud.account.service.RemoteAccountService;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.account.struct.AccountStruct;
import com.hqy.cloud.account.struct.RegistryAccountStruct;
import com.hqy.cloud.apps.blog.converter.Converter;
import com.hqy.cloud.apps.blog.dto.AccountRegistryDTO;
import com.hqy.cloud.apps.blog.dto.BlogUserProfileDTO;
import com.hqy.cloud.apps.blog.dto.ForgetPasswordDTO;
import com.hqy.cloud.apps.blog.service.request.UserRequestService;
import com.hqy.cloud.apps.blog.vo.AccountProfileVO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.foundation.common.account.AccountAuthRandomCodeServer;
import com.hqy.cloud.foundation.common.account.AccountRandomCodeServer;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.cloud.rpc.thrift.struct.CommonResultStruct;
import com.hqy.cloud.service.EmailRemoteService;
import com.hqy.cloud.web.common.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.hqy.cloud.common.result.ResultCode.USER_NOT_FOUND;
import static com.hqy.cloud.common.result.ResultCode.VERIFY_CODE_ERROR;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 17:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRequestServiceImpl implements UserRequestService {

    private final AccountRandomCodeServer randomCodeServer = new AccountAuthRandomCodeServer();

    @Override
    public R<AccountProfileVO> getUserProfile(Long id) {
        AccountInfoDTO account = AccountRpcUtil.getAccount(id);
        if (account == null || account.getId() == null) {
            return R.failed(USER_NOT_FOUND);
        }
        return R.ok(Converter.INSTANCE.convert(account));
    }

    @Override
    public R<Boolean> updateUserProfile(BlogUserProfileDTO profile) {
        String avatar = profile.getAvatar();
        log.info("Upload user avatar: {}, id = {}.", avatar, profile.getId());
        AccountProfileStruct struct = AccountProfileStruct.builder()
                .id(profile.getId())
                .avatar(avatar)
                .birthday(profile.getBirthday())
                .nickname(profile.getNickname())
                .intro(profile.getIntro())
                .sex(profile.getSex()).build();
        RemoteAccountProfileService remoteService = RpcClient.getRemoteService(RemoteAccountProfileService.class);
        boolean update = remoteService.uploadAccountProfile(struct);
        return update ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> sendEmailCode(String usernameOrEmail) {
        RemoteAccountService remoteService = RpcClient.getRemoteService(RemoteAccountService.class);
        AccountStruct account = remoteService.getAccountByUsernameOrEmail(usernameOrEmail);
        if (Objects.isNull(account) || Objects.isNull(account.id)) {
            return R.failed(USER_NOT_FOUND);
        }
        String code = randomCodeServer.randomCode(StrUtil.EMPTY, account.email, 6);
        EmailRemoteService emailRemoteService = RpcClient.getRemoteService(EmailRemoteService.class);
        emailRemoteService.sendVerifyCodeEmail(usernameOrEmail, code);
        return R.ok();
    }


    @Override
    public R<Boolean> registryAccount(AccountRegistryDTO registry) {
        //校验邮箱验证码
        if (!randomCodeServer.isExist(StrUtil.EMPTY, registry.getEmail(), registry.getCode())) {
            return R.failed(VERIFY_CODE_ERROR);
        }
        //RPC注册账号
        RemoteAccountService accountRemoteService = RpcClient.getRemoteService(RemoteAccountService.class);
        CommonResultStruct commonResultStruct = accountRemoteService.registryAccount(new RegistryAccountStruct(registry.getUsername(), registry.getEmail(), registry.getPassword()));
        if (!commonResultStruct.isResult()) {
            return R.failed(commonResultStruct.message, commonResultStruct.code);
        }
        return R.ok();
    }

    @Override
    public R<Boolean> sendRegistryEmail(String email) {
        //判断邮箱是否被注册
        RemoteAccountService service = RpcClient.getRemoteService(RemoteAccountService.class);
        Long id = service.getAccountIdByUsernameOrEmail(email);
        if (Objects.nonNull(id)) {
            return R.failed(ResultCode.EMAIL_EXIST);
        }
        // 生成验证码
        String code = randomCodeServer.randomCode(StrUtil.EMPTY, email, 6);
        //RPC 发送验证码
        EmailRemoteService emailRemoteService = RpcClient.getRemoteService(EmailRemoteService.class);
        emailRemoteService.sendRegistryEmail(email, code);
        if (log.isDebugEnabled()) {
            log.debug("Send registry email code: {} to {}.", code, email);
        }
        return R.ok();
    }

    @Override
    public R<Boolean> resetPassword(ForgetPasswordDTO passwordDTO) {
        RemoteAccountService remoteService = RpcClient.getRemoteService(RemoteAccountService.class);
        String email = passwordDTO.getUsernameOrEmail();
        if (!Validator.isEmail(email)) {
            //query account
            AccountStruct struct = remoteService.getAccountByUsernameOrEmail(email);
            if (Objects.isNull(struct)) {
                return R.failed(USER_NOT_FOUND);
            }
            email = struct.email;
        }
        //校验邮箱验证码是否准确
        if (!randomCodeServer.isExist(StrUtil.EMPTY, email, passwordDTO.getCode())) {
            return R.failed(VERIFY_CODE_ERROR);
        }
        CommonResultStruct commonResultStruct = remoteService.updateAccountPassword(email, passwordDTO.getPassword());
        if (!commonResultStruct.isResult()) {
            return R.failed(commonResultStruct.message, commonResultStruct.code);
        }
        return R.ok();
    }

    @Override
    public R<Boolean> updatePassword(Long accountId, String oldPassword, String newPassword) {
        RemoteAccountService remoteService = RpcClient.getRemoteService(RemoteAccountService.class);
        CommonResultStruct commonResultStruct = remoteService.updateAccountPasswordByIdAndOldPassword(accountId, oldPassword, newPassword);
        if (!commonResultStruct.isResult()) {
            return R.failed(commonResultStruct.message, commonResultStruct.code);
        }
        return R.ok();
    }
}
