package com.hqy.cloud.message.service.impl;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.message.bind.Constants;
import com.hqy.cloud.message.bind.dto.ImUserInfoDTO;
import com.hqy.cloud.message.bind.enums.BlacklistState;
import com.hqy.cloud.message.bind.enums.ImFriendApplicationState;
import com.hqy.cloud.message.bind.enums.ImFriendState;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.cache.ImBlacklistCache;
import com.hqy.cloud.message.cache.ImFriendCache;
import com.hqy.cloud.message.db.entity.Blacklist;
import com.hqy.cloud.message.db.entity.FriendApplication;
import com.hqy.cloud.message.db.entity.FriendRelationship;
import com.hqy.cloud.message.db.entity.FriendState;
import com.hqy.cloud.message.db.service.*;
import com.hqy.cloud.message.service.ImChatMessageService;
import com.hqy.cloud.message.service.ImUserRelationshipService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.concurrent.IExecutorsRepository;
import com.hqy.cloud.util.spring.SpringContextHolder;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImUserRelationshipServiceImpl implements ImUserRelationshipService {
    private final ImFriendCache imFriendCache;
    private final IFriendApplicationService friendApplicationService;
    private final IFriendStateService friendStateService;
    private final IFriendRelationshipService friendRelationshipService;

    private final ImBlacklistCache blacklistCache;
    private final IBlacklistService iBlacklistService;

    private final IUserSettingService userSettingService;
    private final IPrivateConversationService privateConversationService;

    private final TransactionTemplate transactionTemplate;


    @Override
    public boolean isFriend(Long userId, Long anotherId) {
        boolean isFriend = imFriendCache.isFriend(userId, anotherId);
        if (isFriend) {
            return true;
        }
        // 直接插db判断是否是好友关系
        isFriend = friendRelationshipService.isFriend(userId, anotherId);
        if (isFriend) {
            // 只缓存是好友，因为一般只有是好友才会互发消息
            imFriendCache.addFriend(userId, anotherId);
        }
        return isFriend;
    }

    @Override
    public boolean allFriend(Long userId, List<Long> friendIds) {
        return friendRelationshipService.allFriend(userId, friendIds);
    }

    @Override
    public boolean addFriend(Long userId, Long applyId, String message, boolean applied) {
        // 好友申请表实体对象
        List<FriendApplication> applications;
        if (applied) {
            applications = List.of(FriendApplication.of(userId, applyId, ImFriendApplicationState.ACCEPTED.state),
                                    FriendApplication.of(applyId, userId, ImFriendApplicationState.ACCEPTED.state));
        } else {
            applications = List.of(FriendApplication.of(applyId, userId, ImFriendApplicationState.ACCEPTED.state));
        }
        Map<Long, String> usernames = userSettingService.selectUsernames(List.of(applyId, userId));
        if (MapUtils.isEmpty(usernames)) {
            log.warn("Not found usernames by ids: {}.", List.of(userId, applyId));
            return false;
        }
        // 好友状态实体
        List<FriendState> friendStates = List.of(FriendState.of(userId, applyId, ImFriendState.NORMAL.value, usernames.get(applyId)),
                FriendState.of(applyId, userId, ImFriendState.NORMAL.value, usernames.get(userId)));
        // 好友表实体对象
        FriendRelationship relationship = FriendRelationship.of(userId, applyId);

        Boolean execute = transactionTemplate.execute(status -> {
            try {
                // 批量新增或更新好友申请表
                AssertUtil.isTrue(friendApplicationService.insertOrUpdate(applications), "Failed execute to insert or update friend application.");
                // 批量新增或更新好友状态表
                AssertUtil.isTrue(friendStateService.insertOrUpdate(friendStates), "Failed execute insert or update friend state.");
                // 新增好友关系表
                AssertUtil.isTrue(friendRelationshipService.insertOrUpdate(List.of(relationship)), "Failed execute to insert or update friend relationship");
                return true;
            } catch (Throwable cause) {
                log.error(cause.getMessage(), cause);
                status.setRollbackOnly();
                return false;
            }
        });

        if (Boolean.TRUE.equals(execute)) {
            // 异步发送消息.
            IExecutorsRepository.newExecutor(Constants.IM_EXECUTOR_NAME).execute(() -> {
                ImChatMessageService service = SpringContextHolder.getBean(ImChatMessageService.class);
                if (StringUtil.isNotBlank(message)) {
                    service.sendAddFriendMessage(applyId, userId, message);
                }
            });
            return true;
        }
        return false;
    }


    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        Boolean execute = transactionTemplate.execute(status -> {
            try {
                // 修改好友申请表， 将状态改为已过期
                friendApplicationService.updateApplicationStatusByApplyAndReceive(friendId, userId, ImFriendApplicationState.EXPIRED.state);
                // 移除好友关系表
                AssertUtil.isTrue(friendRelationshipService.removeRelationShip(userId, friendId), "Failed execute to remove friend by relation ship.");
                // 移除好友状态表
                AssertUtil.isTrue(friendStateService.removeState(userId, friendId), "Failed execute to remove friend by state.");
                // 移除会话表
                AssertUtil.isTrue(privateConversationService.removeConversation(userId, friendId), "Failed execute to remove conversation.");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });

        if (Boolean.TRUE.equals(execute)) {
            // 删除缓存
            imFriendCache.removeFriend(userId, friendId);
            return true;
        }
        return false;
    }

    @Override
    public BlacklistState getBlacklistState(Long firstId, Long secondId) {
        BlacklistState state = blacklistCache.getBlacklistState(firstId, secondId);
        if (state == null) {
            // 查询数据库.
            state = iBlacklistService.selectBlacklistState(firstId, secondId);
            if (state == BlacklistState.BLACKED_TO) {
                blacklistCache.addBlacklist(firstId, secondId);
            } else if (state == BlacklistState.BLACKED_FROM) {
                blacklistCache.addBlacklist(secondId, firstId);
            }
        }
        return state;
    }

    @Override
    public boolean addBlacklist(Long userId, Long blackId) {
        Blacklist blacklist = Blacklist.builder().userId(userId).blackId(blackId).build();
        boolean save = iBlacklistService.save(blacklist);
        if (save) {
            blacklistCache.addBlacklist(userId, blackId);
        }
        return save;
    }

    @Override
    public boolean removeBlacklist(Long userId, Long blackId) {
        boolean result = iBlacklistService.removeByUserIdAndBlackId(userId, blackId);
        if (result) {
            blacklistCache.removeBlacklist(userId, blackId);
        }
        return result;
    }

    @Override
    public Map<Long, UserInfoVO> selectFriendMessageVO(Long userId, Long friendId) {
        // 好友的消息
        ImUserInfoDTO friendInfo = userSettingService.selectFriendInfoById(userId, friendId);
        UserInfoVO friendVO = new UserInfoVO(friendInfo.getId().toString(), StringUtil.isBlank(friendInfo.getRemark()) ? friendInfo.getNickname() : friendInfo.getRemark(), friendInfo.getAvatar());
        HashMap<Long, UserInfoVO> resultMap = MapUtil.newHashMap(2);
        resultMap.put(friendInfo.getId(), friendVO);
        resultMap.put(userId, new UserInfoVO(userId.toString()));
        return resultMap;
    }



}
