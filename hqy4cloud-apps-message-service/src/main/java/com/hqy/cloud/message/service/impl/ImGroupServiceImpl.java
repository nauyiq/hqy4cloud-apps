package com.hqy.cloud.message.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.apps.commom.constants.AppsConstants;
import com.hqy.cloud.message.bind.Constants;
import com.hqy.cloud.message.bind.ImLanguageContext;
import com.hqy.cloud.message.bind.PropertiesConstants;
import com.hqy.cloud.message.bind.dto.*;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.message.bind.enums.GroupRole;
import com.hqy.cloud.message.bind.event.support.AddGroupMemberEvent;
import com.hqy.cloud.message.bind.event.support.ContactNameChangeEvent;
import com.hqy.cloud.message.bind.event.support.GroupNoticeEvent;
import com.hqy.cloud.message.bind.event.support.RemoveGroupMemberEvent;
import com.hqy.cloud.message.bind.vo.GroupMemberVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.cache.ImGroupMemberCache;
import com.hqy.cloud.message.db.entity.Group;
import com.hqy.cloud.message.db.entity.GroupConversation;
import com.hqy.cloud.message.db.entity.GroupMember;
import com.hqy.cloud.message.db.entity.UserSetting;
import com.hqy.cloud.message.db.service.IGroupConversationService;
import com.hqy.cloud.message.db.service.IGroupMemberService;
import com.hqy.cloud.message.db.service.IGroupService;
import com.hqy.cloud.message.db.service.IUserSettingService;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.service.ImChatMessageService;
import com.hqy.cloud.message.service.ImGroupService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.concurrent.IExecutorsRepository;
import com.hqy.cloud.util.spring.SpringContextHolder;
import com.hqy.cloud.web.common.UploadResult;
import com.hqy.cloud.web.upload.UploadFileService;
import com.hqy.cloud.web.upload.UploadResponse;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hqy.cloud.util.ImageUtil.MAX_FILE_SIZE;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImGroupServiceImpl implements ImGroupService {
    private final ImGroupMemberCache groupMemberCache;
    private final IGroupService groupService;
    private final IGroupMemberService groupMemberService;
    private final IGroupConversationService groupConversationService;
    private final IUserSettingService userSettingService;

    private final ImEventListener imEventListener;
    private final UploadFileService uploadFileService;
    private final TransactionTemplate template;

    @Override
    public Set<Long> getGroupMembers(Long groupId) {
        Set<Long> members = groupMemberCache.getGroupMembers(groupId);
        if (members == null) {
            // 从数据库中查询
            members = groupMemberService.getGroupMemberIds(groupId);
            if (CollectionUtils.isEmpty(members)) {
                return null;
            } else {
                groupMemberCache.addGroupMembers(groupId, members);
            }
        }
        return members;
    }

    @Override
    public Map<Long, UserInfoVO> getGroupMemberUserInfo(Long groupId, Set<Long> groupMemberIds) {
        Set<ImUserInfoDTO> userInfos = groupMemberService.getGroupMemberUserInfo(groupId, groupMemberIds);
        return userInfos.stream()
                .map(user -> new UserInfoVO(user.getId().toString(), StringUtil.isBlank(user.getRemark()) ? user.getNickname() : user.getRemark(), user.getAvatar()))
                .collect(Collectors.toMap(u -> Long.parseLong(u.getId()), Function.identity()));
    }

    @Override
    public Map<Long, List<Long>> getGroupMemberIdMaps(Set<Long> groupIds) {
        List<GroupMemberIdsDTO> membersResult = groupMemberService.getGroupMembers(groupIds);
        if (CollectionUtils.isEmpty(membersResult)) {
            return MapUtil.empty();
        }
        return null;
    }

    @Override
    public boolean createGroup(Long creator, GroupDTO createGroup, List<UserSetting> userSettings) {
        List<Long> ids = createGroup.getUserIds();
        ids.add(creator);
        // 构造群聊表实体
        Map<Long, UserSetting> map = userSettings.stream().collect(Collectors.toMap(UserSetting::getId, Function.identity()));
        String groupName = getGroupName(creator, createGroup.getName(), map);
        String avatar = getGroupAvatar(creator, ids, map);
        Group group = Group.of(groupName, creator, avatar, createGroup);

        // 入库返回群聊会话列表
        List<GroupConversation> conversations = template.execute(status -> {
            try {
                AssertUtil.isTrue(groupService.save(group), "Failed execute to insert group.");
                List<GroupMember> groupMembers = GroupMember.of(creator, ids, group.getId(), map, new Date());
                AssertUtil.isTrue(groupMemberService.saveBatch(groupMembers), "Failed execute to save batch group member.");
                List<GroupConversation> groupConversations = GroupConversation.of(creator, ids, group.getId(), map);
                groupConversations = groupConversationService.insertOrUpdateReturnConversations(groupConversations);
                AssertUtil.notEmpty(groupConversations, "Failed execute to save batch group conversation.");
                return groupConversations;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return null;
            }
        });

        if (CollectionUtils.isEmpty(conversations)) {
            return false;
        }
        // 异步添加事件消息和发送新增会话事件
        IExecutorsRepository.newExecutor(Constants.IM_EXECUTOR_NAME).execute(() -> {
            UserSetting setting = map.get(creator);
            UserInfoVO userInfoVO = new UserInfoVO(creator.toString(), setting.getUsername(), setting.getNickname(), setting.getAvatar());
            groupConversationService.sendAppendGroupChatEvent(conversations, avatar, groupName, creator);
            ImChatMessageService chatMessageService = SpringContextHolder.getBean(ImChatMessageService.class);
            chatMessageService.addEventMessage(true, userInfoVO, group.getId(), ids, EventMessageType.EVENT_CREATE_GROUP);
        });
        return true;
    }


    @Override
    public boolean editGroup(Long id, String username, GroupMemberDTO groupMemberInfo, GroupDTO editGroup) {
        Long groupId = editGroup.getGroupId();
        String groupName = editGroup.getName();
        boolean editGroupName = StringUtils.isNotBlank(groupName) && !groupName.equals(groupMemberInfo.getGroupName());
        String notice = editGroup.getNotice();
        boolean editGroupNotice = StringUtils.isNotBlank(notice) && !notice.equals(groupMemberInfo.getNotice());
        Group group = new Group();
        group.setId(groupId);
        if (editGroupName) {
            group.setName(groupName);
        }
        if (editGroupNotice) {
            group.setNotice(notice);
        }
        boolean update = groupService.updateById(group);
        if (update) {
            // 获取所有群聊成员
            Set<Long> ids = groupMemberService.getGroupMemberIds(groupId);
            // 排除自己
            ids.remove(id);
            asyncNotifyEditGroupInfoChange(id, username, ids, groupId, editGroupName, editGroupNotice, groupName, notice);
        }
        return update;
    }

    @Override
    public boolean isGroupMember(Long groupId, Long userId) {
        Boolean member = groupMemberCache.isGroupMember(userId, groupId);
        if (member == null || !member) {
            // 正常情况下, 不是群聊成员不会调用此方法, 因此不是群聊成员的时候再去库里查一遍，提高缓存命中率
            member = groupMemberService.isGroupMember(groupId, userId);
            if (member) {
                groupMemberCache.addGroupMember(userId, groupId);
            }
        }
        return Boolean.TRUE.equals(member);
    }

    @Override
    public boolean addGroupMembers(GroupMemberDTO groupMemberInfo, List<Long> userIds) {
        Long groupId = groupMemberInfo.getGroupId();
        List<AddGroupMemberDTO> addGroupMembers =  userSettingService.selectAddGroupMembers(groupId, userIds);
        if (CollectionUtils.isEmpty(addGroupMembers)) {
            return false;
        }
        Map<Long, AddGroupMemberDTO> map = addGroupMembers.stream().collect(Collectors.toMap(AddGroupMemberDTO::getId, Function.identity()));
        // 过滤已经是群成员的用户
        userIds = userIds.stream().filter(id -> {
            AddGroupMemberDTO addGroupMember = map.get(id);
            return addGroupMember != null && !addGroupMember.getIsGroupMember();
        }).toList();
        if (CollectionUtils.isEmpty(userIds)) {
            return true;
        }
        List<GroupMember> groupMembers = GroupMember.of(userIds, groupId, map);
        List<GroupConversation> resultList = template.execute(status -> {
            try {
                AssertUtil.isTrue(groupMemberService.insertOrUpdate(groupMembers), "Failed execute to insert ot update group members");
                List<GroupConversation> conversations = GroupConversation.of(groupMembers);
                List<GroupConversation> groupConversations = groupConversationService.insertOrUpdateReturnConversations(conversations);
                AssertUtil.notEmpty(groupConversations, "Failed execute to insert ot update group conversations");
                return groupConversations;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return Collections.emptyList();
            }
        });
        if (CollectionUtils.isNotEmpty(resultList)) {
            List<Long> finalUserIds = userIds;
            IExecutorsRepository.newExecutor(Constants.IM_EXECUTOR_NAME).execute(() -> {
                // 1. 发送新增会话事件.
                groupConversationService.sendAppendGroupChatEvent(resultList, groupMemberInfo.getGroupAvatar(), groupMemberInfo.getGroupName(), groupMemberInfo.getGroupCreator());
                // 2. 发送新增群聊成员事件.
                String now = DateUtil.formatDateTime(new Date());
                // 新增的增员.
                List<GroupMemberVO> vos = finalUserIds.stream().map(id -> {
                    AddGroupMemberDTO addGroupMember = map.get(id);
                    UserInfoVO vo = new UserInfoVO(id.toString(), addGroupMember.getUsername(), addGroupMember.getAvatar());
                    return new GroupMemberVO(id.toString(), GroupRole.COMMON.role, now, vo);
                }).toList();
                // 获取所有的群聊成员.
                Set<Long> groupMemberIds = groupMemberService.getGroupMemberIds(groupId);
                AddGroupMemberEvent event = AddGroupMemberEvent.of(groupMemberIds.stream().map(Objects::toString).toList(), groupId.toString(), vos);
                imEventListener.onAddGroupMemberEvent(event);
            });
            return true;
        }
        return false;
    }


    @Override
    public boolean removeGroupMember(GroupMemberDTO member) {
        Long groupId = member.getGroupId();
        Long userId = member.getId();
        // 删除缓存
        groupMemberCache.removeGroupMember(userId, groupId);
        Date updated = new Date();
        Boolean execute = template.execute(status -> {
            try {
                // 移除群聊成员， 伪删除
                AssertUtil.isTrue(groupMemberService.removeMember(groupId, userId
                ), "Failed execute to remove group member.");
                // 修改群聊会话角色, 表示当前角色是被删除的
                AssertUtil.isTrue(groupConversationService.updateGroupConversationRoleAndUpdated(groupId, userId, GroupRole.REMOVED.role, updated), "Failed execute to update group conversation.");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return false;
            }
        });
        if (Boolean.TRUE.equals(execute)) {
            try {
                // 再次删除缓存
                groupMemberCache.removeGroupMember(userId, groupId);
            } catch (Throwable cause) {
                log.error("Failed execute to remove group member cache, cause: {}", cause.getMessage());
            }
            // 异步发送消息
            IExecutorsRepository.newExecutor(Constants.IM_EXECUTOR_NAME).execute(() -> {
                // 通知所有群成员，包括被移除的自己
                Set<Long> memberIds = groupMemberService.getGroupMemberIds(groupId);
                memberIds.add(userId);
                UserInfoVO fromUser = new UserInfoVO(userId.toString(), member.getDisplayName(), member.getDisplayName(), StrUtil.EMPTY);
                // 发送移除群聊事件消息
                RemoveGroupMemberEvent event = RemoveGroupMemberEvent.of(memberIds.stream().map(Objects::toString).toList(), groupId.toString(), userId.toString());
                imEventListener.onRemoveGroupMemberEvent(event);
                // 添加事件消息
                ImChatMessageService chatMessageService = SpringContextHolder.getBean(ImChatMessageService.class);
                chatMessageService.addEventMessage(true, fromUser, groupId, memberIds, EventMessageType.EVENT_GROUP_MEMBER_REMOVED, updated);
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean exitGroup(GroupMemberDTO groupMemberInfo) {
        Long userId = groupMemberInfo.getId();
        Long groupId = groupMemberInfo.getGroupId();
        // 删除缓存
        groupMemberCache.removeGroupMember(userId, groupId);
        Boolean execute = template.execute(status -> {
            try {
                // 移除群聊成员
                removeGroupMember(groupId, userId);
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return false;
            }
        });
        if (Boolean.TRUE.equals(execute)) {
            try {
                // 再次删除缓存
                groupMemberCache.removeGroupMember(userId, groupId);
                IExecutorsRepository.newExecutor(Constants.IM_EXECUTOR_NAME).execute(() -> {
                    Set<Long> groupMemberIds = groupMemberService.getGroupMemberIds(groupId);
                    RemoveGroupMemberEvent event = RemoveGroupMemberEvent.of(groupMemberIds.stream().map(Objects::toString).toList(), groupId.toString(), userId.toString());
                    imEventListener.onRemoveGroupMemberEvent(event);
                });
            } catch (Throwable cause) {
                log.error("Failed execute to remove group member cache, cause: {}", cause.getMessage());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteGroup(GroupMemberDTO groupMemberInfo) {
        Long userId = groupMemberInfo.getId();
        Long groupId = groupMemberInfo.getGroupId();
        groupMemberCache.removeGroupAllMembers(groupId);
        // 获取所有群聊成员.
        Set<Long> memberIds = groupMemberService.getGroupMemberIds(groupId);
        Date updated = new Date();
        Boolean execute = template.execute(status -> {
            try {
                // 删除创建者群聊会话
                AssertUtil.isTrue(groupConversationService.removeGroupConversation(groupId, userId), "Failed execute to remove group member.");
                // 删除群聊
                AssertUtil.isTrue(groupService.deletedGroup(groupId), "Failed execute to delete group.");
                // 移除群聊成员
                AssertUtil.isTrue(groupMemberService.removeMember(groupId, null), "Failed execute to remove group member.");
                // 修改群聊成员会话角色
                AssertUtil.isTrue(groupConversationService.updateGroupConversationRoleAndUpdated(groupId, null, GroupRole.REMOVED.role, updated), "Failed execute to update group conversation.");
                // 删除群聊消息，数据量大的情况下应该考虑页裂、页合并的影响
//                AssertUtil.isTrue(chatMessageService.deleteGroupMessages(groupId), "Failed execute to remove group message.");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return false;
            }
        });
        if (Boolean.TRUE.equals(execute)) {
            try {
                // 再次删除缓存
                groupMemberCache.removeGroupAllMembers(groupId);
            } catch (Throwable cause) {
                log.error("Failed execute to remove group cache, cause: {}", cause.getMessage());
            }
            // 异步发送消息
            IExecutorsRepository.newExecutor(Constants.IM_EXECUTOR_NAME).execute(() -> {
                // 排除群主
                memberIds.remove(userId);
                UserInfoVO fromUser = new UserInfoVO(userId.toString(), groupMemberInfo.getDisplayName(),  groupMemberInfo.getDisplayName(),null);
                ImChatMessageService chatMessageService = SpringContextHolder.getBean(ImChatMessageService.class);
                chatMessageService.addEventMessage(true, fromUser, groupId, memberIds, EventMessageType.EVENT_GROUP_REMOVED, updated);
            });
            return true;
        }
        return false;
    }


    private void removeGroupMember(Long groupId, Long userId) {
        AssertUtil.isTrue(groupMemberService.removeMember(groupId, userId), "Failed execute to remove group member.");
        AssertUtil.isTrue(groupConversationService.removeGroupConversation(groupId, userId), "Failed execute to remove group member.");
    }


    private void asyncNotifyEditGroupInfoChange(Long id, String username, Set<Long> ids, Long groupId, boolean editGroupName, boolean editGroupNotice,
                                                String groupName, String notice) {
        IExecutorsRepository.newExecutor(Constants.IM_EXECUTOR_NAME).execute(() -> {
            UserInfoVO sender = new UserInfoVO(id.toString(), username, username, StrUtil.EMPTY);
            ImChatMessageService chatMessageService = SpringContextHolder.getBean(ImChatMessageService.class);
            ids.add(id);
            List<String> notifyUsers = ids.stream().map(Objects::toString).toList();
            if (editGroupName) {
                // 发送事件消息
                chatMessageService.addEventMessage(true, sender, groupId, ids, EventMessageType.EVENT_GROUP_NAME_MODIFIED);
                // 发送联系人名字变更事件
                ContactNameChangeEvent changeEvent = ContactNameChangeEvent.of(true, notifyUsers, groupId.toString(), groupName);
                imEventListener.onContactNameChangeEvent(changeEvent);
            }
            if (editGroupNotice) {
                chatMessageService.addEventMessage(true, sender, groupId, ids, EventMessageType.EVENT_GROUP_NOTICE_MODIFIED);
                // 发送群聊公告变更事件
                GroupNoticeEvent event = GroupNoticeEvent.of(notifyUsers, groupId.toString(), notice);
                imEventListener.onGroupNoticeChangeEvent(event);
            }
        });
    }

    private String getGroupName(Long creator, String groupName, Map<Long, UserSetting> map) {
        if (StringUtils.isNotBlank(groupName)) {
            return groupName;
        }
        UserSetting setting = map.get(creator);
        String value = ImLanguageContext.getValue(PropertiesConstants.GROUP_NAME_DEFAULT_PREFIX_KEY);
        return setting.getNickname() + value;
    }

    private String getGroupAvatar(Long creator, List<Long> ids, Map<Long, UserSetting> map) {
        List<String> generatorAvatarList = new ArrayList<>();
        generatorAvatarList.add(map.get(creator).getAvatar());
        for (Long id : ids) {
            if (generatorAvatarList.size() >= MAX_FILE_SIZE || id.equals(creator)) {
                break;
            }
            generatorAvatarList.add(map.get(id).getAvatar());
        }
        try {
            UploadResponse response = uploadFileService.generateFile(generatorAvatarList, AppsConstants.Message.IM_DEFAULT_GROUP_AVATAR_FOLDER);
            UploadResult result = response.getResult();
            return result.isResult() ? result.getRelativePath() : Constants.IM_DEFAULT_GROUP_AVATAR;
        } catch (Throwable cause) {
            log.error("Failed execute to generate group avatar.");
            log.error(cause.getMessage(), cause);
            return Constants.IM_DEFAULT_GROUP_AVATAR;
        }
    }


}
