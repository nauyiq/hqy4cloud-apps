package com.hqy.cloud.message.service.impl;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.foundation.common.account.AccountAvatarUtil;
import com.hqy.cloud.message.bind.ImMessageConverter;
import com.hqy.cloud.message.bind.dto.ImUserSettingInfoDTO;
import com.hqy.cloud.message.bind.vo.UserCardVO;
import com.hqy.cloud.message.bind.vo.UserImSettingVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.db.entity.FriendState;
import com.hqy.cloud.message.db.entity.UserSetting;
import com.hqy.cloud.message.db.service.IFriendStateService;
import com.hqy.cloud.message.db.service.IUserSettingService;
import com.hqy.cloud.message.service.ImUserRelationshipService;
import com.hqy.cloud.message.service.request.ImUserSettingRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImUserSettingRequestServiceImpl implements ImUserSettingRequestService {
    private final IUserSettingService iUserSettingService;
    private final IFriendStateService iFriendStateService;
    private final ImUserRelationshipService imUserRelationshipService;

    @Override
    public R<UserImSettingVO> getUserImSettingInfo(Long accountId) {
        UserSetting userSetting = iUserSettingService.getById(accountId);
        if (userSetting == null) {
            return R.failed(ResultCode.USER_NOT_FOUND);
        }
        UserImSettingVO vo = ImMessageConverter.CONVERTER.convert(userSetting);
        vo.setAvatar(AccountAvatarUtil.getAvatar(userSetting.getAvatar()));
        return R.ok(vo);
    }

    @Override
    public R<Boolean> updateUserImSettingInfo(Long accountId, UserImSettingVO setting) {
        UserSetting userSetting = iUserSettingService.getById(accountId);
        if (userSetting == null) {
            return R.failed(ResultCode.USER_NOT_FOUND);
        }
        userSetting.setInviteGroup(setting.getIsInviteGroup());
        userSetting.setQueryAccount(setting.getIsQueryAccount());
        userSetting.setSyncSetting(setting.getIsSyncSetting());
        boolean update = iUserSettingService.updateById(userSetting);
        return update ? R.ok() : R.failed(ResultCode.SYSTEM_BUSY);
    }

    @Override
    public R<Boolean> updateImUserInfo(Long accountId, ImUserSettingInfoDTO userInfo) {
        UserSetting userSetting = iUserSettingService.getById(accountId);
        if (userSetting == null) {
            return R.failed(ResultCode.USER_NOT_FOUND);
        }
        ImMessageConverter.CONVERTER.update(userInfo, userSetting);
        userSetting.setAvatar(AccountAvatarUtil.extractAvatar(userInfo.getAvatar()));
        boolean result;
        // 判断是否开启同步聊天设置.
        Boolean syncSetting = userSetting.getSyncSetting();
        if (syncSetting == null || Boolean.TRUE.equals(syncSetting)) {
            result = iUserSettingService.syncUpdateUserInfo(userSetting);
        } else {
            result = iUserSettingService.updateById(userSetting);
        }
        return result ? R.ok() : R.failed();
    }

    @Override
    public R<UserCardVO> getImUserInfo(Long userId, Long friendId) {
        // 查询用户聊天信息
        UserSetting userSetting = iUserSettingService.getById(friendId);
        if (userSetting == null) {
            return R.failed(ResultCode.USER_NOT_FOUND);
        }
        UserCardVO userCardVO = new UserCardVO(friendId.toString(), userSetting.getUsername(), userSetting.getNickname(),
                AccountAvatarUtil.getAvatar(userSetting.getAvatar()), userSetting.getIntro());
        if (imUserRelationshipService.isFriend(userId, friendId)) {
            // 是好友, 查询好友状态表
            FriendState state = iFriendStateService.getByUserIdAndFriendId(userId, friendId);
            userCardVO.setFriend(new UserCardVO.FriendVO(state.getTop(), state.getNotice(), state.getRemark()));
        }
        return R.ok(userCardVO);
    }

    @Override
    public R<List<UserInfoVO>> searchImUsers(Long accountId, String name) {
        List<UserSetting> userSettings = iUserSettingService.selectImUsersByName(name).stream().filter(user -> !user.getId().equals(accountId)).toList();
        if (CollectionUtils.isEmpty(userSettings)) {
            return R.ok(Collections.emptyList());
        }
        List<UserInfoVO> userInfos = userSettings.stream().map(userSetting ->
                new UserInfoVO(userSetting.getId().toString(), userSetting.getUsername(), userSetting.getNickname(),
                        AccountAvatarUtil.getAvatar(userSetting.getAvatar()))).toList();
        return R.ok(userInfos);
    }
}
