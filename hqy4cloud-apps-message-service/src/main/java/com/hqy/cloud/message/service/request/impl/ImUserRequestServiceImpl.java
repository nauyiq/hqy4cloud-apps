package com.hqy.cloud.message.service.request.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.account.service.RemoteAccountProfileService;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.account.struct.AccountStruct;
import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.ImMessageConverter;
import com.hqy.cloud.message.bind.dto.ContactsDTO;
import com.hqy.cloud.message.bind.dto.FriendApplicationDTO;
import com.hqy.cloud.message.bind.dto.FriendDTO;
import com.hqy.cloud.message.bind.dto.GroupContactDTO;
import com.hqy.cloud.message.bind.event.support.ContactNameChangeEvent;
import com.hqy.cloud.message.bind.event.support.FriendApplicationEvent;
import com.hqy.cloud.message.bind.vo.*;
import com.hqy.cloud.message.cache.ImRelationshipCacheService;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.service.ImFriendOperationsService;
import com.hqy.cloud.message.service.ImMessageOperationsService;
import com.hqy.cloud.message.service.request.ImUserRequestService;
import com.hqy.cloud.message.tk.entity.ImFriend;
import com.hqy.cloud.message.tk.entity.ImFriendApplication;
import com.hqy.cloud.message.tk.entity.ImUserSetting;
import com.hqy.cloud.message.tk.service.ImFriendApplicationTkService;
import com.hqy.cloud.message.tk.service.ImFriendTkService;
import com.hqy.cloud.message.tk.service.ImGroupTkService;
import com.hqy.cloud.message.tk.service.ImUserSettingTkService;
import com.hqy.cloud.rpc.nacos.client.RPCClient;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.thread.ParentExecutorService;
import com.hqy.cloud.web.common.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

/**
 * ImUserRequestService
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
    private final ImGroupTkService groupTkService;
    private final ImEventListener imEventListener;
    private final ImUserSettingTkService userSettingTkService;
    private final ImFriendApplicationTkService applicationTkService;
    private final ImRelationshipCacheService relationshipCacheService;
    private final ImMessageOperationsService messageOperationsService;

    @Override
    public R<UserImSettingVO> getUserImSetting(Long id) {
        ImUserSetting imUserSetting = userSettingTkService.queryById(id);
        if (imUserSetting == null) {
            // insert default im setting.
            imUserSetting = ImUserSetting.of(id);
            userSettingTkService.insert(imUserSetting);
        }
        UserImSettingVO vo = UserImSettingVO.of(imUserSetting);
        return R.ok(vo);
    }

    @Override
    public R<Boolean> updateUserImSetting(Long userId, UserImSettingVO setting) {
        ImUserSetting imUserSetting = new ImUserSetting(userId);
        imUserSetting.setOline(setting.getIsOnline());
        imUserSetting.setInviteGroup(setting.getIsInviteGroup());
        imUserSetting.setPrivateChat(setting.getIsPrivateChat());
        imUserSetting.setClearMsg(setting.getIsClearMsg());
        imUserSetting.setClearMsgDate(setting.getClearMessageDate());
        return userSettingTkService.updateSelective(imUserSetting) ? R.ok() : R.failed();
    }

    @Override
    public R<List<FriendVO>> getImFriends(Long id) {
        List<ImFriend> imFriends = friendTkService.queryList(ImFriend.of(id, null));
        if (CollectionUtils.isEmpty(imFriends)) {
            return R.ok(Collections.emptyList());
        }
        List<Long> ids = imFriends.parallelStream().map(ImFriend::getUserId).toList();
        Map<Long, AccountProfileStruct> map = AccountRpcUtil.getAccountProfileMap(ids);
        List<FriendVO> vos = imFriends.stream().map(friend -> {
            Long userId = friend.getUserId();
            AccountProfileStruct struct = map.get(userId);
            if (struct == null) {
                return null;
            }
            return FriendVO.builder()
                    .id(friend.getUserId().toString())
                    .avatar(struct.avatar)
                    .username(struct.username)
                    .displayName(StringUtils.isBlank(friend.getRemark()) ? struct.nickname : friend.getRemark())
                    .build();
        }).filter(Objects::nonNull).toList();
        return R.ok(vos);
    }

    @Override
    public R<UserCardVO> getImUserCardInfo(Long id, Long userId) {
        //query info by userId.
        AccountInfoDTO accountInfo = AccountRpcUtil.getAccount(userId);
        if (accountInfo == null) {
            return R.failed(ResultCode.USER_NOT_FOUND);
        }
        //query user setting vo.
        ImUserSetting imUserSetting = userSettingTkService.queryById(userId);
        UserCardVO vo = new UserCardVO(accountInfo.getId().toString(), accountInfo.getUsername(), accountInfo.getNickname(), accountInfo.getAvatar(), accountInfo.getIntro(),
                imUserSetting != null && imUserSetting.getPrivateChat(), imUserSetting == null || imUserSetting.getInviteGroup());
        if (id != null) {
            // query is friend.
            ImFriend imFriend = friendTkService.queryOne(ImFriend.of(id, userId));
            if (imFriend != null) {
                UserCardVO.FriendVO friendVO = new UserCardVO.FriendVO(imFriend.getTop(), imFriend.getNotice(), imFriend.getRemark());
                vo.setFriend(friendVO);
            }
        }
        return R.ok(vo);
    }

    @Override
    public R<List<UserInfoVO>> searchImUsers(Long id, String name) {
        RemoteAccountProfileService profileService = RPCClient.getRemoteService(RemoteAccountProfileService.class);
        List<AccountProfileStruct> profiles = profileService.getAccountProfilesByName(name);
        List<UserInfoVO> vos;
        if (CollectionUtils.isEmpty(profiles)) {
            vos = Collections.emptyList();
        } else {
            vos = profiles.stream().filter(profile -> !profile.id.equals(id)).map(ImMessageConverter.CONVERTER::convert).toList();
        }
        return R.ok(vos);
    }

    @Override
    public R<ContactsVO> getUserImContacts(Long userId) {
        List<ContactVO> contacts = new ArrayList<>();
        // query group contacts.
        List<GroupContactDTO> groupContacts = groupTkService.queryGroupContact(userId);
        if (CollectionUtils.isNotEmpty(groupContacts)) {
            contacts.addAll(groupContacts.parallelStream().map(ContactVO::new).toList());
        }
        // query friend contacts.
        ContactsDTO contact = friendTkService.queryContactByUserId(userId);
        if (contact == null) {
            return R.ok(ContactsVO.of(0, contacts));
        } else {
            int unread = contact.getUnread() == null ? 0 : contact.getUnread();
            List<ImFriend> imFriends = contact.getContacts();
            if (CollectionUtils.isNotEmpty(imFriends)) {
                List<Long> ids = imFriends.parallelStream().map(ImFriend::getUserId).toList();
                Map<Long, AccountProfileStruct> structMap = AccountRpcUtil.getAccountProfileMap(ids);
                List<ContactVO> vos = imFriends.parallelStream().map(friend -> {
                    AccountProfileStruct struct = structMap.get(friend.getUserId());
                    if (struct == null) {
                        return null;
                    }
                    return ContactVO.builder()
                            .id(friend.getUserId().toString())
                            .displayName(StringUtils.isBlank(friend.getRemark()) ? struct.nickname : friend.getRemark())
                            .avatar(struct.avatar)
                            .isGroup(false)
                            .isNotice(friend.getNotice())
                            .isTop(friend.getTop())
                            .index(friend.getIndex()).build();
                }).filter(Objects::nonNull).toList();
                contacts.addAll(vos);
            }
            return R.ok(ContactsVO.of(unread, contacts));
        }
    }

    @Override
    public R<List<UserApplicationVO>> queryApplications(Long userId) {
        List<ImFriendApplication> applications = applicationTkService.queryFriendApplications(userId);
        if (CollectionUtils.isEmpty(applications)) {
            return R.ok(Collections.emptyList());
        }
        List<Long> ids = getUserIds(userId, applications);
        Map<Long, AccountProfileStruct> profileMap = AccountRpcUtil.getAccountProfileMap(ids);
        if (MapUtil.isEmpty(profileMap)) {
            // search account rpc info is empty.
            log.warn("Search account rpc return empty, ids: {}", ids);
            return R.ok(Collections.emptyList());
        }
        Map<Long, ImFriendApplication> resultMap = MapUtil.newHashMap(applications.size());
        List<ImFriendApplication> unreadApplications = new ArrayList<>();
        List<UserApplicationVO> vos = new ArrayList<>();
        for (ImFriendApplication application : applications) {
            Long displayUserId = userId.equals(application.getApply()) ? application.getReceive() : application.getApply();
            AccountProfileStruct struct = profileMap.get(displayUserId);
            if (struct == null) {
                continue;
            }
            if (resultMap.containsKey(displayUserId)) {
                // 去除重复用户的申请, 或只展示最新申请的用户信息.
                ImFriendApplication friendApplication = resultMap.get(displayUserId);
                if (friendApplication.getCreated().getTime() >= application.getCreated().getTime()) {
                    continue;
                }
            }
            UserApplicationVO vo = UserApplicationVO.builder()
                    .id(application.getId())
                    .receive(application.getReceive().toString())
                    .send(application.getApply().toString())
                    .remark(application.getRemark())
                    .status(application.getStatus())
                    .info(new UserInfoVO(struct.id.toString(), struct.username, struct.nickname, struct.avatar, null)).build();
            if (application.getStatus() == null) {
                unreadApplications.add(application);
                vo.setStatus(ImFriendApplication.NOT_VERIFY);
            }
            vos.add(vo);
        }
        if (CollectionUtils.isNotEmpty(unreadApplications)) {
            ParentExecutorService.getInstance().execute(() ->
                    applicationTkService.updateApplicationStatus(unreadApplications.parallelStream().map(ImFriendApplication::getId).toList(), ImFriendApplication.NOT_VERIFY));
        }
        return R.ok(vos);
    }

    @NotNull
    private List<Long> getUserIds(Long userId, List<ImFriendApplication> applications) {
        List<Long> ids = applications.parallelStream().map(ImFriendApplication::getApply).filter(id -> !id.equals(userId)).toList();
        List<Long> userIds = applications.parallelStream().map(ImFriendApplication::getReceive).filter(id -> !id.equals(userId)).toList();
        List<Long> allUserIds = new ArrayList<>(ids);
        allUserIds.addAll(userIds);
        return allUserIds.stream().distinct().toList();
    }

    @Override
    public R<Boolean> addImFriend(Long id, FriendDTO add) {
        Long userId = add.getUserId();
        if (imFriendOperationsService.isFriend(id, userId)) {
            return R.ok();
        }
        AccountStruct struct = AccountRpcUtil.getAccountInfo(add.getUserId());
        if (struct == null || !struct.getStatus()) {
            return R.failed(ResultCode.USER_NOT_FOUND);
        }
        FriendApplicationDTO friendApplication = applicationTkService.queryApplicationStatus(id, userId);
        boolean result;
        int unread = friendApplication.getUnread();
        if (friendApplication.getStatus() != null && friendApplication.getStatus().equals(ImFriendApplication.NOT_VERIFY)) {
            // 说明对方也申请添加好友 则直接添加该好友.
            result = Boolean.TRUE.equals(template.execute(status -> {
                try {
                    AssertUtil.isTrue(applicationTkService.updateSelective(ImFriendApplication.of(id, ImFriendApplication.AGREE)), "Failed execute to update friend application.");
                    AssertUtil.isTrue(imFriendOperationsService.addFriend(userId, id, StrUtil.EMPTY), "Failed execute to add friend.");
                    return true;
                } catch (Throwable cause) {
                    log.error(cause.getMessage(), cause);
                    status.setRollbackOnly();
                    return false;
                }
            }));
        } else {
            ImFriendApplication application = ImFriendApplication.of(id, add.getUserId() , add.getRemark(), null);
            result = applicationTkService.insertDuplicate(application);
            unread++;
            if (result && StringUtils.isNotBlank(add.getRemark())) {
                //新增系统消息
                ParentExecutorService.getInstance().execute(() -> messageOperationsService.addSystemMessage(id, userId, add.getRemark(), null));
            }
        }
        if (result) {
            return imEventListener.onAddFriendApplicationEvent(FriendApplicationEvent.of(userId.toString(), unread)) ? R.ok() : R.failed();
        }
        return R.failed();
    }

    @Override
    public R<Boolean> acceptOrRejectImFriend(Long id, FriendDTO friendDTO) {
        //查询好友申请表
        ImFriendApplication application = applicationTkService.queryById(friendDTO.getApplicationId());
        if (application == null || id.equals(application.getApply())) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        Integer status = application.getStatus();
        if (status != null && status != ImFriendApplication.NOT_VERIFY) {
            return R.ok();
        }
        application.setStatus(friendDTO.getStatus() ? ImFriendApplication.AGREE : ImFriendApplication.REFUSE);
        application.setRemark(friendDTO.getRemark());

        //拒接添加好友的申请
        if (!friendDTO.getStatus()) {
            return applicationTkService.updateSelective(application) ? R.ok() : R.failed();
        }
        Boolean execute = template.execute(transactionStatus -> {
            try {
                AssertUtil.isTrue(applicationTkService.update(application), "Failed execute to update friend application.");
                if (friendDTO.getStatus()) {
                    AssertUtil.isTrue(imFriendOperationsService.addFriend(application.getApply(), id, friendDTO.getRemark()), "Failed execute to add friend operations.");
                }
                return true;
            } catch (Throwable cause) {
                transactionStatus.setRollbackOnly();
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
        ImFriend friend = ImFriend.of(id, userId);
        friend.setRemark(mark);
        if (friendTkService.updateSelective(friend)) {
            relationshipCacheService.addFriendRelationship(id, userId, mark);
            imEventListener.onContactNameChangeEvent(ContactNameChangeEvent.of(false, Collections.singletonList(id.toString()), userId.toString(), mark));
            return R.ok();
        }
        return R.failed();
    }

    @Override
    public R<Boolean> removeFriend(Long id, Long userId) {
        return imFriendOperationsService.removeFriend(id, userId) ? R.ok() : R.failed();
    }
}
