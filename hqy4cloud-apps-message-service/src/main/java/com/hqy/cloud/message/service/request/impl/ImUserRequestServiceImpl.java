package com.hqy.cloud.message.service.request.impl;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.dto.FriendDTO;
import com.hqy.cloud.message.bind.vo.UserImSettingVO;
import com.hqy.cloud.message.service.ImFriendOperationsService;
import com.hqy.cloud.message.service.request.ImUserRequestService;
import com.hqy.cloud.message.tk.entity.ImFriend;
import com.hqy.cloud.message.tk.entity.ImFriendApplication;
import com.hqy.cloud.message.tk.entity.ImUserSetting;
import com.hqy.cloud.message.tk.service.ImFriendApplicationTkService;
import com.hqy.cloud.message.tk.service.ImFriendTkService;
import com.hqy.cloud.message.tk.service.ImUserSettingTkService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.web.common.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author qiyuan.hong
 * @date 2023-08-12 12:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImUserRequestServiceImpl implements ImUserRequestService {
    private final TransactionTemplate template;
    private final ImFriendOperationsService imFriendOperationsService;
    private final ImFriendTkService friendTkService;
    private final ImUserSettingTkService userSettingTkService;
    private final ImFriendApplicationTkService applicationTkService;

    @Override
    public R<UserImSettingVO> getUserImSetting(Long id) {
        ImUserSetting imUserSetting = userSettingTkService.queryById(id);
        UserImSettingVO vo = imUserSetting == null ? UserImSettingVO.of() : UserImSettingVO.of(imUserSetting);
        return R.ok(vo);
    }

    @Override
    public R<Boolean> addImFriend(Long id, FriendDTO add) {
        AccountInfoDTO accountInfo = AccountRpcUtil.getAccountInfo(add.getUserId());
        if (accountInfo == null || !accountInfo.getStatus()) {
            return R.failed(ResultCode.USER_NOT_FOUND);
        }
        ImFriendApplication application = ImFriendApplication.of(add.getUserId(), id, add.getRemark());
        int i = applicationTkService.insertDuplicate(application);
        return i > 0 ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> acceptOrRejectImFriend(Long id, FriendDTO friendDTO) {
        //查询好友申请表
        ImFriendApplication application = ImFriendApplication.of(id, friendDTO.getUserId());
        application = applicationTkService.queryOne(application);
        if (application == null) {
            return R.failed(ResultCode.DATA_EMPTY);
        }
        //状态已经变更过.
        if (application.getStatus() != null) {
            return R.ok();
        }
        application.setStatus(friendDTO.getStatus());
        application.setRemark(friendDTO.getRemark());

        //拒接添加好友的申请
        if (!friendDTO.getStatus()) {
            return applicationTkService.updateSelective(application) ? R.ok() : R.failed();
        }

        ImFriendApplication finalApplication = application;
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(applicationTkService.updateSelective(finalApplication), "Failed execute to update friend application.");
                if (friendDTO.getStatus()) {
                    AssertUtil.isTrue(imFriendOperationsService.addFriend(finalApplication), "Failed execute to add friend operations.");
                }
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });
        return Boolean.TRUE.equals(execute) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> updateFriendMark(Long id, Long userId, String mark) {
        if (!imFriendOperationsService.isFriend(id, userId)) {
            return R.failed(AppsResultCode.IM_NOT_FRIEND);
        }
        ImFriend friend = ImFriend.of(id, userId, true);
        friend.setMark(mark);
        return friendTkService.updateSelective(friend) ? R.ok() : R.failed();
    }

    @Override
    public R<Boolean> removeFriend(Long id, Long userId) {
        return imFriendOperationsService.removeFriend(id, userId) ? R.ok() : R.failed();
    }
}
