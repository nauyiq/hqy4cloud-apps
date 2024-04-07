package com.hqy.cloud.message.service.impl;

import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.Constants;
import com.hqy.cloud.message.bind.dto.FriendApplicationDTO;
import com.hqy.cloud.message.bind.dto.FriendDTO;
import com.hqy.cloud.message.bind.enums.BlacklistState;
import com.hqy.cloud.message.bind.enums.ImFriendApplicationState;
import com.hqy.cloud.message.bind.event.support.FriendApplicationEvent;
import com.hqy.cloud.message.bind.vo.FriendApplicationVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.db.entity.FriendApplication;
import com.hqy.cloud.message.db.entity.FriendState;
import com.hqy.cloud.message.db.entity.UserSetting;
import com.hqy.cloud.message.db.service.IFriendApplicationService;
import com.hqy.cloud.message.db.service.IFriendStateService;
import com.hqy.cloud.message.db.service.IPrivateConversationService;
import com.hqy.cloud.message.db.service.IUserSettingService;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.service.request.ImFriendRequestService;
import com.hqy.cloud.message.service.ImUserRelationshipService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.concurrent.IExecutorsRepository;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImFriendRequestServiceImpl implements ImFriendRequestService {
    private final IUserSettingService iUserSettingService;
    private final ImUserRelationshipService imUserRelationshipService;
    private final IFriendStateService iFriendStateService;
    private final IFriendApplicationService iFriendApplicationService;
    private final IPrivateConversationService privateConversationService;

    private final TransactionTemplate transactionTemplate;
    private final ImEventListener eventListener;

    @Override
    public R<List<FriendApplicationVO>> getFriendApplications(Long loginId) {
        List<FriendApplicationDTO> applications = iFriendApplicationService.queryApplicationByUserId(loginId);
        if (CollectionUtils.isEmpty(applications)) {
            return R.ok(Collections.emptyList());
        }

        // 存在未读消息, 则设置为已读 异步设置.
        if (applications.stream().anyMatch(f -> f.getStatus() != null && f.getStatus().equals(ImFriendApplicationState.UN_READ.state))) {
            IExecutorsRepository.newExecutor(Constants.IM_EXECUTOR_NAME).execute(() -> {
                List<Long> ids = applications.stream().map(FriendApplicationDTO::getId).toList();
                iFriendApplicationService.updateApplicationsStatus(ids, ImFriendApplicationState.ALREADY_READ.state);
            });
        }

        List<FriendApplicationVO> vos = applications.stream().map(state -> FriendApplicationVO.builder()
                .id(state.getId())
                .status(state.getStatus())
                .applyRemark(state.getRemark())
                .created(DateUtil.formatDateTime(state.getCreated()))
                .info(new UserInfoVO(state.getUserId().toString(), state.getUsername(), state.getNickname(), state.getAvatar())).build()).filter(Objects::nonNull).toList();
        return R.ok(vos);
    }

    @Override
    public R<Boolean> applyAddFriend(Long userId, Long friendId, String remark) {
        // 查找用户是否存在.
        UserSetting friend = iUserSettingService.getById(friendId);
        if (friend == null) {
            return R.failed(ResultCode.USER_NOT_FOUND);
        }
        // 判断是否已经是好友, 如果已经是好友申请列表无需新增
        if (imUserRelationshipService.isFriend(userId, friendId)) {
            return R.ok();
        }
        // 如果被对方拉黑也无法申请添加好友
        BlacklistState blacklistState = imUserRelationshipService.getBlacklistState(userId, friendId);
        if (blacklistState != null && blacklistState.state.equals(BlacklistState.BLACKED_FROM.state)) {
            return R.failed(AppsResultCode.IM_BLACKLIST_FROM);
        }

        FriendApplication application = iFriendApplicationService.getByApplyAndReceive(userId, friendId);
        if (application != null && (ImFriendApplicationState.UN_READ.state.equals(application.getStatus()) || ImFriendApplicationState.ALREADY_READ.state.equals(application.getStatus())) ) {
            return R.ok();
        }

        // 判断对方是否也申请过好友, 如果申请过好友并且还有效则直接添加好友
        if (iFriendApplicationService.checkIsRequestApplicationAndStateIsValid(friendId, userId)) {
            // 直接添加好友
            boolean addFriend = imUserRelationshipService.addFriend(userId, friendId, remark, true);
            return addFriend ? R.ok() : R.failed();
        }

        // 构造申请好友对象实体
         application = FriendApplication.of(userId, friendId, remark);
        // 申请表实体入库, 成功发送申请好友事件
        if (iFriendApplicationService.insertOrUpdate(List.of(application))) {
            boolean event = eventListener.onAddFriendApplicationEvent(FriendApplicationEvent.of(friendId.toString()));
            if (!event) {
                log.warn("Failed execute to send add friendApplication message, applicationId: {}.", application.getId());
            }
            return R.ok();
        }
        return R.failed();
    }

    @Override
    public R<Boolean> acceptOrRejectFriendApplication(Long accountId, Long applicationId, Boolean status) {
        // 判断好友申请是否存在 并且接收人是不是当前登录用户id
        FriendApplication application = iFriendApplicationService.getById(applicationId);
        if (application == null || !application.getReceive().equals(accountId)) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        // 判断状态是否有效
        Integer applicationStatus = application.getStatus();
        if (ImFriendApplicationState.ACCEPTED.state.equals(applicationStatus) ||
                ImFriendApplicationState.REJECTED.state.equals(applicationStatus) ||
                ImFriendApplicationState.EXPIRED.state.equals(applicationStatus)) {
            return R.failed(AppsResultCode.IM_APPLICATION_INVALID);
        }
        if (Boolean.FALSE.equals(status)) {
            // 如果是拒绝好友申请, 直接修改申请表状态即可
            application.setStatus(ImFriendApplicationState.REJECTED.state);
            return iFriendApplicationService.updateById(application) ? R.ok() : R.failed();
        }
        return imUserRelationshipService.addFriend(accountId, application.getApply(), application.getRemark(), false) ? R.ok() : R.failed();
    }


    @Override
    public R<Boolean> updateFriendInfo(Long accountId, FriendDTO friend) {
        Long friendId = friend.getUserId();
        FriendState friendState = iFriendStateService.getByUserIdAndFriendId(accountId, friendId);
        if (friendState == null) {
            // 判断好友状态是否存在.
            return R.failed(AppsResultCode.IM_NOT_FRIEND);
        }
        String remark = friend.getRemark();
        if (StringUtil.isBlank(remark)) {
            // 查找用户昵称
            remark = iUserSettingService.selectUsernames(List.of(friendId)).get(friendId);
        }
        friendState.setRemark(remark);
        String displayName = remark;
        Boolean execute = transactionTemplate.execute(status -> {
            try {
                // 更新状态表中的备注名
                AssertUtil.isTrue(iFriendStateService.updateById(friendState), "Failed execute to update friend state.");
                // 更新会话表中的展示名
                AssertUtil.isTrue(privateConversationService.updateConversationDisplayName(accountId, friendId, displayName), "Failed execute to update friend state.");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return false;
            }
        });
        return Boolean.TRUE.equals(execute) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> removeFriend(Long accountId, Long friendId) {
        // 判断好友状态是否存在.
        FriendState friendState = iFriendStateService.getByUserIdAndFriendId(accountId, friendId);
        if (friendState == null) {
            return R.failed(AppsResultCode.IM_NOT_FRIEND);
        }
        return imUserRelationshipService.removeFriend(accountId, friendId) ? R.ok() : R.failed();
    }
}
