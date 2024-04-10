package com.hqy.cloud.message.service.transactional.impl;

import com.hqy.cloud.foundation.common.account.AccountAvatarUtil;
import com.hqy.cloud.message.bind.Constants;
import com.hqy.cloud.message.bind.dto.ImUserInfoDTO;
import com.hqy.cloud.message.db.entity.UserSetting;
import com.hqy.cloud.message.db.service.IUserSettingService;
import com.hqy.cloud.message.service.transactional.TccAddImUserService;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
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
@LocalTCC
@RequiredArgsConstructor
public class TccAddImUserServiceImpl implements TccAddImUserService {
    private final IUserSettingService iUserSettingService;

    @Override
    @TwoPhaseBusinessAction(name = Constants.SEATA_TRANSACTION_ADD_IM_USER, useTCCFence = true)
    public boolean addImUser(@BusinessActionContextParameter(Constants.SEATA_TCC_IM_USER_CONTEXT) ImUserInfoDTO userInfo) {
        try {
            UserSetting userSetting = new UserSetting(userInfo.getUsername(),
                    AccountAvatarUtil.extractAvatar(userInfo.getAvatar()), userInfo.getNickname(), userInfo.getRemark());
            userSetting.setId(userInfo.getId());
            userSetting.setStatus(false);
            return iUserSettingService.save(userSetting);
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }
    }

    @Override
    public boolean commit(BusinessActionContext actionContext) {
        ImUserInfoDTO user = actionContext.getActionContext(Constants.SEATA_TCC_IM_USER_CONTEXT, ImUserInfoDTO.class);
        if (user == null) {
            return false;
        }
        return iUserSettingService.settingUserStatusEnabled(user.getId());
    }

    @Override
    public boolean rollback(BusinessActionContext actionContext) {
        ImUserInfoDTO user = actionContext.getActionContext(Constants.SEATA_TCC_IM_USER_CONTEXT, ImUserInfoDTO.class);
        if (user != null) {
            // 判断try阶段是否成功
            UserSetting setting = iUserSettingService.getById(user.getId());
            if (setting == null) {
                return true;
            }
            return iUserSettingService.removeById(user.getId());
        }
        return true;
    }
}
