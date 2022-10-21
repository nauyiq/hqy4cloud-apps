package com.hqy.blog.service.impl.request;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.service.remote.AccountProfileRemoteService;
import com.hqy.account.service.remote.AccountRemoteService;
import com.hqy.account.struct.AccountProfileStruct;
import com.hqy.account.struct.AccountStruct;
import com.hqy.account.struct.RegistryAccountStruct;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.dto.AccountBaseRegistryDTO;
import com.hqy.blog.dto.AccountRegistryDTO;
import com.hqy.blog.dto.BlogUserProfileDTO;
import com.hqy.blog.dto.ForgetPasswordDTO;
import com.hqy.blog.service.request.UserRequestService;
import com.hqy.blog.vo.AccountProfileVO;
import com.hqy.communication.service.mail.EmailRemoteService;
import com.hqy.fundation.common.AccountRandomCodeServer;
import com.hqy.fundation.common.account.AccountRegistryAccountRandomCodeServer;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import com.hqy.rpc.thrift.struct.CommonResultStruct;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import com.hqy.util.ValidationUtil;
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
public class UserRequestServiceImpl implements UserRequestService {

    private final AccountRandomCodeServer randomCodeServer = new AccountRegistryAccountRandomCodeServer();

    @Override
    public DataResponse getLoginUserInfo(Long id) {
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        String accountInfoJson = accountRemoteService.getAccountInfoJson(id);
        AssertUtil.notEmpty(accountInfoJson, CommonResultCode.USER_NOT_FOUND.message);

        AccountInfoDTO accountInfoDTO = JsonUtil.toBean(accountInfoJson, AccountInfoDTO.class);
        return CommonResultCode.dataResponse(CommonResultCode.SUCCESS,
                new AccountProfileVO(accountInfoDTO.getId().toString(), accountInfoDTO.getUsername(), accountInfoDTO.getNickname(), accountInfoDTO.getAvatar(), accountInfoDTO.getIntro(), accountInfoDTO.getBirthday()));
    }


    @Override
    public DataResponse updateLoginUserInfo(BlogUserProfileDTO profile) {
        AccountProfileRemoteService accountProfileRemoteService = RPCClient.getRemoteService(AccountProfileRemoteService.class);
        boolean update = accountProfileRemoteService.uploadAccountProfile(new AccountProfileStruct(profile.getId(), profile.getNickname(), profile.getAvatar(), profile.getAvatar(), profile.getBirthday()));
        if (!update) {
            return CommonResultCode.dataResponse(CommonResultCode.SYSTEM_ERROR_UPDATE_FAIL);
        }
        return CommonResultCode.dataResponse();
    }

    @Override
    public MessageResponse sendRegistryEmail(AccountBaseRegistryDTO registry) {
        String email = registry.getEmail();
        if (!ValidationUtil.validateEmail(email)) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_EMAIL);
        }
        //check registry info.
        AccountRemoteService service = RPCClient.getRemoteService(AccountRemoteService.class);
        CommonResultStruct result = service.checkRegistryInfo(registry.getUsername(), email);
        if (!result.result) {
            return CommonResultCode.messageResponse(result.code, result.message);
        }
        //get email code.
        String code = randomCodeServer.randomCode(registry.getUsername(), email, 6);
        //send email.
        EmailRemoteService emailRemoteService = RPCClient.getRemoteService(EmailRemoteService.class);
        emailRemoteService.sendRegistryEmail(email, registry.getUsername(), code);

        return CommonResultCode.messageResponse();
    }

    @Override
    public MessageResponse registryAccount(AccountRegistryDTO registry) {
        //校验邮箱验证码
        if (!randomCodeServer.isExist(registry.getUsername(), registry.getEmail(), registry.getCode())) {
            return CommonResultCode.messageResponse(CommonResultCode.VERIFY_CODE_ERROR);
        }
        //RPC注册账号
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        CommonResultStruct commonResultStruct = accountRemoteService.registryAccount(new RegistryAccountStruct(registry.getUsername(), registry.getEmail(), registry.getPassword()));
        if (!commonResultStruct.isResult()) {
            return CommonResultCode.messageResponse(CommonResultCode.SYSTEM_BUSY);
        }
        return CommonResultCode.messageResponse();
    }

    @Override
    public MessageResponse sendForgetPasswordEmail(String usernameOrEmail) {
        //校验用户名或者邮箱是否存在
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        AccountStruct struct = accountRemoteService.getAccountStructByUsernameOrEmail(usernameOrEmail);
        if (struct == null || struct.id == null) {
            return CommonResultCode.messageResponse(CommonResultCode.USER_NOT_FOUND);
        }
        //生成邮箱校验码
        String code = randomCodeServer.randomCode(usernameOrEmail, usernameOrEmail, 6);
        //send email.
        EmailRemoteService emailRemoteService = RPCClient.getRemoteService(EmailRemoteService.class);
        emailRemoteService.sendRegistryEmail(struct.email, struct.getUsername(), code);
        return CommonResultCode.messageResponse();
    }

    @Override
    public MessageResponse resetPassword(ForgetPasswordDTO passwordDTO) {
        String usernameOrEmail = passwordDTO.getUsernameOrEmail();
        //校验邮箱验证码是否准确
        if (!randomCodeServer.isExist(usernameOrEmail, usernameOrEmail, passwordDTO.getCode())) {
            return CommonResultCode.messageResponse(CommonResultCode.VERIFY_CODE_ERROR);
        }
        //修改用户密码
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        CommonResultStruct commonResultStruct = accountRemoteService.updateAccountPassword(usernameOrEmail, passwordDTO.getPassword());
        if (!commonResultStruct.isResult()) {
            return CommonResultCode.messageResponse(commonResultStruct.code, commonResultStruct.message);
        }
        return CommonResultCode.messageResponse();
    }

    @Override
    public MessageResponse updatePassword(Long accountId, String oldPassword, String newPassword) {
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        CommonResultStruct commonResultStruct = accountRemoteService.updateAccountPasswordByIdAndOldPassword(accountId, oldPassword, newPassword);
        if (!commonResultStruct.isResult()) {
            return CommonResultCode.messageResponse(commonResultStruct.code, commonResultStruct.message);
        }
        return CommonResultCode.messageResponse();
    }
}
