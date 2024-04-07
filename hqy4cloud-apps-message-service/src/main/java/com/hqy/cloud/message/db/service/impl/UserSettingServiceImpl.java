package com.hqy.cloud.message.db.service.impl;

import com.hqy.cloud.account.service.RemoteAccountProfileService;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.db.mybatisplus.BasePlusServiceImpl;
import com.hqy.cloud.message.bind.Constants;
import com.hqy.cloud.message.bind.ImMessageConverter;
import com.hqy.cloud.message.bind.dto.AddGroupMemberDTO;
import com.hqy.cloud.message.bind.dto.ImUserInfoDTO;
import com.hqy.cloud.message.db.entity.UserSetting;
import com.hqy.cloud.message.db.mapper.UserSettingMapper;
import com.hqy.cloud.message.db.service.IUserSettingService;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.cloud.util.AssertUtil;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * im用户设置表 服务实现类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-04
 */
@Service
public class UserSettingServiceImpl extends BasePlusServiceImpl<UserSetting, UserSettingMapper> implements IUserSettingService {


    @Override
    public List<UserSetting> selectImUsersByName(String name) {
        return baseMapper.selectImUserByName(name);
    }

    @Override
    public ImUserInfoDTO selectFriendInfoById(Long id, Long friendId) {
        return baseMapper.selectFriendInfoById(id, friendId);
    }

    @Override
    public Map<Long, ImUserInfoDTO> selectFriendInfosByUserIdAndFriendId(Long userId, Long friendId) {
        List<ImUserInfoDTO> userInfos = baseMapper.selectFriendInfosByUserIdAndFriendId(userId, friendId);
        return userInfos.stream().collect(Collectors.toMap(ImUserInfoDTO::getId, Function.identity()));
    }

    @Override
    public String selectImUserNickname(Long id) {
        return baseMapper.selectNicknameById(id);
    }

    @Override
    public Map<Long, String> selectUsernames(List<Long> ids) {
        List<UserSetting> list = baseMapper.selectUsernames(ids);
        return list.stream().collect(Collectors.toMap(UserSetting::getId, UserSetting::getUsername));
    }

    @Override
    public boolean settingUserStatusEnabled(Long id) {
        return baseMapper.settingUserStatusEnabled(id) > 0;
    }

    @Override
    public List<AddGroupMemberDTO> selectAddGroupMembers(Long groupId, List<Long> userIds) {
        List<AddGroupMemberDTO> list = baseMapper.selectAddGroupMembers(groupId, userIds);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        list.forEach(info -> info.setIsGroupMember(info.getUserId() != null));
        return list;
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class, name = Constants.SEATA_TRANSACTION_SYNC_USER_PROFILE)
    public boolean syncUpdateUserInfo(UserSetting userSetting) {
        // 更新im用户设置表
        AssertUtil.isTrue(updateById(userSetting), "Failed execute to update user info.");
        // 账号RPC更新账号信息
        AccountProfileStruct struct = ImMessageConverter.CONVERTER.convertProfileStruct(userSetting);
        RemoteAccountProfileService remoteService = RpcClient.getRemoteService(RemoteAccountProfileService.class);
        AssertUtil.isTrue(remoteService.transactionalUploadAccountProfile(struct),  "Failed execute to remote update account profile.");
        return true;
    }
}
