package com.hqy.cloud.message.service.impl;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.apps.commom.constants.AppsConstants;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.message.bind.ConvertUtil;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.bind.enums.GroupRole;
import com.hqy.cloud.message.bind.event.support.*;
import com.hqy.cloud.message.bind.vo.*;
import com.hqy.cloud.message.cache.ImRelationshipCacheService;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.service.ImGroupOperationsService;
import com.hqy.cloud.message.service.ImMessageOperationsService;
import com.hqy.cloud.message.tk.entity.*;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImGroupMemberTkService;
import com.hqy.cloud.message.tk.service.ImGroupTkService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.web.common.AccountRpcUtil;
import com.hqy.cloud.web.common.UploadResult;
import com.hqy.cloud.web.upload.UploadFileService;
import com.hqy.cloud.web.upload.UploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_DEFAULT_GROUP_AVATAR;
import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_MESSAGE_SUCCESS;
import static com.hqy.cloud.util.ImageUtil.MAX_FILE_SIZE;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:37
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImGroupOperationsServiceImpl implements ImGroupOperationsService {
    private final TransactionTemplate template;
    private final ImEventListener eventListener;
    private final ImGroupTkService groupTkService;
    private final ImMessageOperationsService messageOperationsService;
    private final ImGroupMemberTkService groupMemberTkService;
    private final ImConversationTkService conversationTkService;
    private final ImRelationshipCacheService relationshipCacheService;
    private final UploadFileService uploadFileService;

    @Override
    public boolean createGroup(Long id, GroupDTO createGroup, List<ImFriend> friends) {
        // 调用账号RPC查询用户账号profile.
        List<Long> userIds = createGroup.getUserIds();
        userIds.add(id);
        Map<Long, AccountProfileStruct> profileMap = AccountRpcUtil.getAccountProfileMap(userIds);
        if (MapUtil.isEmpty(profileMap) || profileMap.size() != userIds.size()) {
            log.warn("Account rpc not found profiles by ids:{}.", userIds);
            return false;
        }
        String avatar = getGroupAvatar(id, userIds, profileMap);
        String groupName = getGroupName(id, createGroup, profileMap);
        ImGroup group = ImGroup.of(groupName, id, avatar, new Date());
        List<ImConversation> conversations = template.execute(status -> {
            try {
                // insert group.
                AssertUtil.isTrue(groupTkService.insert(group), "Failed execute to insert group.");
                Long groupId = group.getId();
                // insert group members
                List<ImGroupMember> groupMembers = ImGroupMember.of(groupId, id, userIds);
                AssertUtil.isTrue(groupMemberTkService.insertList(groupMembers), "Failed execute to insert group members.");
                // insert conversation.
                List<ImConversation> insertConversations = ImConversation.ofGroup(groupId, userIds);
                AssertUtil.isTrue(conversationTkService.insertList(insertConversations), "Failed execute to insert conversations by create group.");
                // 新增事件消息
                messageOperationsService.addSimpleMessage(id, groupId, true, null, insertConversations.parallelStream().map(ImConversation::getUserId).toList(),
                        ImMessageType.EVENT, AppsConstants.Message.CREATOR_GROUP_EVENT_CONTENT, System.currentTimeMillis());
                return insertConversations;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return null;
            }
        });
        if (CollectionUtils.isNotEmpty(conversations)) {
            sendAppendGroupChatEvent(id, true, group.getId(),  group.getInvite(), group.getCreator(), profileMap.get(group.getCreator()).nickname, groupName, group.getAvatar(), conversations);
            return true;
        }
        return false;
    }

    private String getGroupName(Long id, GroupDTO createGroup, Map<Long, AccountProfileStruct> profileMap) {
        if (StringUtils.isBlank(createGroup.getName())) {
            //生成默认群聊名字
            List<Long> userIds = createGroup.getUserIds();
            if (userIds.size() >= MAX_FILE_SIZE) {
                userIds = userIds.stream().limit(MAX_FILE_SIZE - 1).collect(Collectors.toList());
            }
            List<String> names = userIds.stream().map(user -> profileMap.get(user).nickname).toList();
            return StringUtils.join(names, "、");
        } else {
            return createGroup.getName();
        }
    }

    private String getGroupAvatar(Long creator, List<Long> userIds, Map<Long, AccountProfileStruct> profileMap) {
        List<String> generatorAvatarList = new ArrayList<>();
        generatorAvatarList.add(profileMap.get(creator).avatar);
        for (Long userId : userIds) {
            if (generatorAvatarList.size() >= MAX_FILE_SIZE) {
                break;
            }
            if (userId.equals(creator)) {
                continue;
            }
            generatorAvatarList.add(profileMap.get(userId).avatar);
        }
        try {
            UploadResponse response = uploadFileService.generateFile(generatorAvatarList, AppsConstants.Message.IM_DEFAULT_GROUP_AVATAR_FOLDER);
            UploadResult result = response.getResult();
            return result.isResult() ? result.getRelativePath() : IM_DEFAULT_GROUP_AVATAR;
        } catch (Throwable cause) {
            log.error("Failed execute to generate group avatar.");
            log.error(cause.getMessage(), cause);
            return IM_DEFAULT_GROUP_AVATAR;
        }
    }


    private void sendAppendGroupChatEvent(Long operator, boolean isAdd, Long groupId, Boolean invite, Long creator, String creatorName, String groupName, String groupAvatar,
                                          List<ImConversation> conversations) {
        List<AppendChatEvent> appendChatEvents;
        if (isAdd) {
            appendChatEvents = conversations.parallelStream().map(conversation -> {
                Long userId = conversation.getUserId();
                if (userId.equals(operator)) {
                    ConversationVO conversationVO = buildGroupConversationVO(invite, creator, creatorName, groupName, groupAvatar , conversation);
                    ContactVO contactVO = buildGroupContactVO(groupId, groupAvatar, groupName, invite, conversation);
                    return AppendChatEvent.of(operator.toString(), conversationVO, contactVO);
                } else {
                    return AppendChatEvent.of(conversation.getUserId().toString(),
                            buildGroupConversationVO(invite, creator, creatorName, groupName, groupAvatar, conversation), null);
                }
            }).toList();
        } else {
            appendChatEvents = conversations.parallelStream().map(conversation -> AppendChatEvent.of(conversation.getUserId().toString(),
                    buildGroupConversationVO(invite, creator, creatorName, groupName, groupAvatar, conversation), null)).toList();
        }
        boolean result = eventListener.onImAppendGroupChatEvent(appendChatEvents);
        if (log.isDebugEnabled()) {
            log.debug("Do send append group event, group id: {}, result:{}.", groupId, result);
        }
    }

    private ContactVO buildGroupContactVO(Long groupId, String groupAvatar, String groupName, Boolean invite, ImConversation conversation) {
        return ContactVO.builder()
                .id(groupId.toString())
                .isGroup(true)
                .isNotice(conversation.getNotice())
                .isTop(conversation.getTop())
                .avatar(groupAvatar)
                .displayName(groupName)
                .index(AppsConstants.Message.IM_GROUP_DEFAULT_INDEX)
                .isInvite(invite)
                .build();
    }

    private ConversationVO buildGroupConversationVO(Boolean invite, Long creator, String creatorName, String groupName, String groupAvatar, ImConversation conversation) {
        return ConversationVO.builder()
                .conversationId(conversation.getId().toString())
                .id(conversation.getContactId().toString())
                .invite(invite)
                .lastSendTime(conversation.getLastMessageTime() == null ? conversation.getCreated().getTime() : conversation.getLastMessageTime().getTime())
                .unread(1)
                .creator(creator.toString())
                .creatorName(creatorName)
                .isGroup(true)
                .isNotice(conversation.getNotice())
                .isTop(conversation.getTop())
                .role(creator.equals(conversation.getUserId()) ? GroupRole.CREATOR.role : GroupRole.COMMON.role)
                .displayName(groupName)
                .avatar(groupAvatar)
                .build();
    }

    @Override
    public boolean editGroup(AuthenticationInfo userInfo, GroupMemberDTO info, GroupDTO editGroup) {
        boolean editGroupName = true;
        boolean editNotice = true;
        String name = editGroup.getName();
        String notice = editGroup.getNotice();
        if (StringUtils.isBlank(name) || name.equals(info.getGroupName())) {
            editGroupName = false;
        }
        if (StringUtils.isBlank(notice) || notice.equals(info.getNotice())) {
            editNotice = false;
        }
        if (!editGroupName && !editNotice) {
            return true;
        }
        Long groupId = info.getGroupId();
        ImGroup group = ImGroup.of(groupId);
        if (editGroupName) {
            group.setName(name);
        }
        if (editNotice) {
            group.setNotice(notice);
        }
        if (groupTkService.updateSelective(group)) {
            Long id = userInfo.getId();
            String editor = userInfo.getName();
            List<ImGroupMember> groupMembers = groupMemberTkService.queryList(ImGroupMember.of(groupId));
            List<String> userIds = groupMembers.parallelStream().map(ImGroupMember::getUserId).filter(userId -> !userId.equals(id)).map(Objects::toString).toList();
            if (editGroupName) {
                eventListener.onContactNameChangeEvent(ContactNameChangeEvent.of(true, userIds, groupId.toString(), name, editor));
                messageOperationsService.addSimpleMessage(id, groupId, true, null,
                        null, ImMessageType.EVENT,  AppsConstants.Message.IM_GROUP_NAME_CHANGE_CONTENT + name, System.currentTimeMillis());
            }
            if (editNotice) {
                eventListener.onGroupNoticeChangeEvent(GroupNoticeEvent.of(userIds, groupId.toString(), notice, editor));
                messageOperationsService.addSimpleMessage(id, groupId, true, null,
                        null, ImMessageType.EVENT, AppsConstants.Message.IM_GROUP_NOTICE_CHANGE_CONTENT, System.currentTimeMillis());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean addGroupMember(GroupMemberDTO groupMemberInfo, List<Long> addUserIds) {
        Long groupId = groupMemberInfo.getGroupId();
        // 获取当前群成员.
        List<ImGroupMember> concurrentMembers = groupMemberTkService.simpleQueryAllGroupMembers(groupId);
        Map<Long, ImGroupMember> concurrentMemberMap = concurrentMembers.stream().collect(Collectors.toMap(ImGroupMember::getUserId, e -> e));
        // 重新获取新增的群成员
        addUserIds = addUserIds.stream().filter(id -> !concurrentMemberMap.containsKey(id)).toList();
        if (CollectionUtils.isNotEmpty(addUserIds)) {
            // 是否更新群头像
            boolean updateAvatar = concurrentMembers.size() < MAX_FILE_SIZE;
            // 账号RPC获取群聊用户信息
            List<Long> queryProfileIds = new ArrayList<>(addUserIds);
            if (updateAvatar) {
                queryProfileIds.addAll(concurrentMemberMap.keySet());
            }
            Map<Long, AccountProfileStruct> profileMap = AccountRpcUtil.getAccountProfileMap(queryProfileIds);
            String avatar = updateAvatar ? getGroupAvatar(groupMemberInfo.getGroupCreator(), queryProfileIds, profileMap) : groupMemberInfo.getGroupAvatar();
            List<ImGroupMember> groupMembers = addUserIds.stream().map(id -> ImGroupMember.of(groupId, id, null, GroupRole.COMMON.role)).toList();
            List<ImConversation> groupConversations = addUserIds.stream().map(id -> ImConversation.ofGroup(id, groupId)).toList();
            Boolean execute = template.execute(status -> {
                try {
                    if (updateAvatar) {
                        AssertUtil.isTrue(groupTkService.updateSelective(ImGroup.ofAvatar(groupId, avatar)), "Failed execute to update group avatar.");
                    }
                    AssertUtil.isTrue(groupMemberTkService.insertOrUpdate(groupMembers), "Failed execute to insert group members.");
                    conversationTkService.removeConversations(groupConversations);
                    AssertUtil.isTrue(conversationTkService.insertList(groupConversations), "Failed execute to insert group contact.");
                    return true;
                } catch (Throwable cause) {
                    log.error(cause.getMessage(), cause);
                    status.setRollbackOnly();
                    return false;
                }
            });
            if (Boolean.TRUE.equals(execute)) {
                // 发送新增联系人事件给新增的用户
                sendAppendGroupChatEvent(null, false, groupId, groupMemberInfo.getGroupInvite(), groupMemberInfo.getGroupCreator(), profileMap.get(groupMemberInfo.getGroupCreator()).nickname,
                        groupMemberInfo.getGroupName(), avatar, groupConversations);
                // 发送新增群聊用户事件给群聊成员
                List<GroupMemberVO> vos = ConvertUtil.convertGroupMembers(groupMembers, profileMap);
                AddGroupMemberEvent event = AddGroupMemberEvent.of(concurrentMembers.stream().map(member -> member.getUserId().toString()).toList(), groupId.toString(), vos);
                eventListener.onAddGroupMemberEvent(event);
                return true;
            }
            return false;
        }
        return true;
    }


    @Override
    public boolean removeGroupMember(Long operator, Long userId, Long groupId) {
        // 获取当前群成员.
        List<ImGroupMember> concurrentMembers = groupMemberTkService.simpleQueryAllGroupMembers(groupId);
        boolean updateAvatar = concurrentMembers.size() - 1 < MAX_FILE_SIZE;
        String groupAvatar = null;
        Map<Long, AccountProfileStruct> profileMap;
        if (updateAvatar) {
            List<Long> avatarMember = concurrentMembers.stream().map(ImGroupMember::getUserId).toList();
            profileMap = AccountRpcUtil.getAccountProfileMap(avatarMember);
            // 当前删除用户头像不再参与群聊头像生成
            avatarMember = avatarMember.stream().filter(id -> !id.equals(userId)).toList();
            groupAvatar = getGroupAvatar(operator, avatarMember, profileMap);
        } else {
            profileMap = AccountRpcUtil.getAccountProfileMap(List.of(userId));
        }
        if (MapUtil.isEmpty(profileMap)) {
            return false;
        }
        String finalGroupAvatar = groupAvatar;
        ImMessage message = template.execute(status -> {
            try {
                if (updateAvatar) {
                    AssertUtil.isTrue(groupTkService.updateSelective(ImGroup.ofAvatar(groupId, finalGroupAvatar)), "Failed execute to update group avatar.");
                }
                AssertUtil.isTrue(groupMemberTkService.removeGroupMember(groupId, userId), "Failed execute to delete group member.");
                long removeTime = System.currentTimeMillis();
                AssertUtil.isTrue(conversationTkService.deleteConversation(userId, groupId, true, removeTime), "Failed execute to invalid conversation.");
                // 移除redis数据
                relationshipCacheService.removeGroupMember(groupId, userId);
                // 添加系统消息
                return messageOperationsService.addSimpleMessage(userId, groupId, true, null, null,
                        ImMessageType.EVENT, AppsConstants.Message.IM_GROUP_REMOVE_MEMBER_CONTENT, removeTime);
            } catch (Throwable cause) {
                log.error(cause.getMessage());
                status.setRollbackOnly();
                return null;
            }
        });
        if (message != null) {
            List<String> users = concurrentMembers.parallelStream().map(user -> user.getUserId().toString()).toList();
            String username = profileMap.get(userId).username;
            ImMessageVO vo = ImMessageVO.builder()
                    .id(message.getId().toString())
                    .isRead(true)
                    .isGroup(true)
                    .sendTime(message.getCreated().getTime())
                    .fromUser(new UserInfoVO(userId.toString()))
                    .toContactId(message.getTo().toString())
                    .type(ImMessageType.EVENT.type)
                    .messageId(message.getMessageId())
                    .content(ConvertUtil.getEventMessageContent(operator, username, message.getFrom(), true, message.getContent()))
                    .status(IM_MESSAGE_SUCCESS)
                    .build();
            return eventListener.onRemoveGroupMemberEvent(RemoveGroupMemberEvent.of(users, groupId.toString(), userId.toString(), vo));
        }
        return false;
    }

    @Override
    public boolean isGroupMember(Long id, Long groupId) {
        Boolean result = relationshipCacheService.isGroupMember(groupId, id);
        if (result == null) {
            // query from db.
            ImGroupMember member = groupMemberTkService.queryOne(new ImGroupMember(groupId, id));
            if (member == null) {
                return false;
            }
            relationshipCacheService.addGroupMemberRelationship(groupId, id, StringUtils.isBlank(member.getDisplayName()) ? StringConstants.TRUE: member.getDisplayName());
            return true;
        }
        return result;
    }

    @Override
    public boolean exitGroup(ImGroupMember member) {
        Long groupId = member.getGroupId();
        Long userId = member.getUserId();
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(conversationTkService.delete(ImConversation.of(userId, groupId, true)), "Failed execute to delete conversation.");
                AssertUtil.isTrue(groupMemberTkService.delete(member), "Failed execute to delete group member.");
                relationshipCacheService.removeGroupMember(groupId, userId);
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });
        if (Boolean.TRUE.equals(execute)) {
            // 获取当前群成员.
            List<ImGroupMember> concurrentMembers = groupMemberTkService.simpleQueryAllGroupMembers(groupId);
            List<String> members = concurrentMembers.parallelStream().map(ImGroupMember::getUserId).filter(id -> !id.equals(userId))
                    .map(Object::toString).toList();
            eventListener.onExitGroupMemberEvent(ExitGroupEvent.of(members, groupId.toString(), userId.toString()));
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteGroup(Long userId, Long groupId) {
        // 获取当前群成员.
        List<ImGroupMember> concurrentMembers = groupMemberTkService.simpleQueryAllGroupMembers(groupId);
        if (CollectionUtils.isEmpty(concurrentMembers)) {
            return true;
        }
        ImMessage message = template.execute(status -> {
            try {
                AssertUtil.isTrue(groupTkService.updateSelective(ImGroup.of(groupId, false)), "Failed execute to update group.");
                AssertUtil.isTrue(groupMemberTkService.removeGroupMember(groupId, null), "Failed execute to remove group members.");
                long removeTime = System.currentTimeMillis();
                AssertUtil.isTrue(conversationTkService.deleteConversation(null, groupId, true, removeTime), "Failed execute to invalid conversations.");
                AssertUtil.isTrue(conversationTkService.delete(ImConversation.of(userId, groupId, true)), "Failed execute to invalid conversations.");
                // 添加系统消息
                return messageOperationsService.addSimpleMessage(userId, groupId, true, null,null, ImMessageType.EVENT, AppsConstants.Message.IM_GROUP_DELETE_CONTENT, removeTime);
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return null;
            }
        });
        if (message != null) {
            // 删除redis数据
            relationshipCacheService.removeGroup(groupId);
            // 发送事件
            List<String> users = concurrentMembers.stream().map(member -> member.getUserId().toString()).toList();
            ImMessageVO vo = ImMessageVO.builder()
                    .id(message.getId().toString())
                    .isRead(true)
                    .isGroup(true)
                    .sendTime(message.getCreated().getTime())
                    .fromUser(new UserInfoVO(userId.toString()))
                    .toContactId(message.getTo().toString())
                    .type(ImMessageType.EVENT.type)
                    .messageId(message.getMessageId())
                    .content(message.getContent())
                    .status(IM_MESSAGE_SUCCESS)
                    .build();
            return eventListener.onDeleteGroupEvent(DeleteGroupEvent.of(users, groupId.toString(), userId.toString(), vo));
        }
        return false;
    }

    @Override
    public Map<Long, String> getGroupMemberRemark(Long groupId, List<Long> members) {
        Map<Long, String> resultMap = MapUtil.newHashMap(members.size());
        List<String> groupRemarks = relationshipCacheService.getGroupRemarks(groupId, members);
        if (CollectionUtils.isEmpty(groupRemarks)) {
            return resultMap;
        }
        List<Long> queryDbs = new ArrayList<>();
        for (int i = 0; i < groupRemarks.size(); i++) {
            String remark = groupRemarks.get(i);
            Long memberId = members.get(i);
            if (StringUtils.isBlank(remark) ) {
                queryDbs.add(memberId);
            } else if (!remark.equals(StringConstants.TRUE) && !remark.equals(StringConstants.FALSE)) {
                resultMap.put(memberId, remark);
            }
        }
        //query from db and fresh redis cache.
        if (CollectionUtils.isNotEmpty(queryDbs)) {
            List<ImGroupMember> groupMembers = groupMemberTkService.queryGroupMembers(groupId, queryDbs);
            if (CollectionUtils.isNotEmpty(groupMembers)) {
                Map<Long, String> map = groupMembers.stream().collect(Collectors.
                        toMap(ImGroupMember::getUserId, member -> StringUtils.isBlank(member.getDisplayName()) ? StringConstants.TRUE : member.getDisplayName()));
                relationshipCacheService.addGroupMembersRelationship(groupId, map);
            }
        }
        return resultMap;
    }
}
