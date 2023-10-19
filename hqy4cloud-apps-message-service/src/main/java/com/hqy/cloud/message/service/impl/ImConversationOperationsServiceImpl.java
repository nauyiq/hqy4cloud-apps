package com.hqy.cloud.message.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.foundation.common.account.AvatarHostUtil;
import com.hqy.cloud.message.bind.ConvertUtil;
import com.hqy.cloud.message.bind.dto.ChatDTO;
import com.hqy.cloud.message.bind.dto.GroupContactDTO;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.bind.enums.GroupRole;
import com.hqy.cloud.message.bind.event.support.AppendChatEvent;
import com.hqy.cloud.message.bind.event.support.ImNoticeChatEvent;
import com.hqy.cloud.message.bind.event.support.ImTopChatEvent;
import com.hqy.cloud.message.bind.vo.ContactVO;
import com.hqy.cloud.message.bind.vo.ContactsVO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.bind.vo.ImChatVO;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.service.ImConversationOperationsService;
import com.hqy.cloud.message.service.ImFriendOperationsService;
import com.hqy.cloud.message.service.ImMessageOperationsService;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.entity.ImFriend;
import com.hqy.cloud.message.tk.entity.ImGroupMember;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImFriendTkService;
import com.hqy.cloud.message.tk.service.ImGroupMemberTkService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.web.common.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_GROUP_DEFAULT_INDEX;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/31 16:59
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImConversationOperationsServiceImpl implements ImConversationOperationsService {
    private final TransactionTemplate template;
    private final ImConversationTkService conversationTkService;
    private final ImGroupMemberTkService imGroupMemberTkService;
    private final ImFriendTkService imFriendTkService;
    private final ImFriendOperationsService friendOperationsService;
    private final ImEventListener imEventListener;
    private final ImMessageOperationsService messageOperationsService;


    @Override
    public ImChatVO getImChatInfoList(Long userId) {
        List<ChatDTO> chats = conversationTkService.queryImChatDTO(userId);
        int unread = messageOperationsService.getSystemMessageUnread(userId);
        if (CollectionUtils.isEmpty(chats)) {
            return new ImChatVO(Collections.emptyList(), ContactsVO.of(unread, Collections.emptyList()));
        }
        //获取列表涉及到的用户ids. 统一采用账号RPC查询.
        Set<Long> allUserIds = getAllUserIdsByChats(userId, chats);
        Map<Long, AccountProfileStruct> profileMap = AccountRpcUtil.getAccountProfileMap(new ArrayList<>(allUserIds));
        //构建用户会话列表
        List<ConversationVO> conversations = buildConversations(userId, profileMap, chats);
        //构建通讯录列表
        List<ContactVO> contacts = buildContacts(userId, profileMap, chats);
        ContactsVO contactsVO = ContactsVO.of(unread, contacts);
        return new ImChatVO(conversations, contactsVO);
    }

    private List<ContactVO> buildContacts(Long userId, Map<Long, AccountProfileStruct> profileMap, List<ChatDTO> chats) {
        if (CollectionUtils.isEmpty(chats)) {
            return Collections.emptyList();
        }
        return chats.parallelStream().filter(chat -> chat.getConversation() != null).map(chat -> {
            GroupContactDTO groupContact = chat.getGroupContact();
            ImFriend friend = chat.getFriend();
            if (groupContact != null) {
                if (!groupContact.getCreator().equals(userId)) {
                    return null;
                }
                // 通讯录中的群聊 只展示自己创建的群聊...
                return ContactVO.builder()
                        .id(groupContact.getGroupId().toString())
                        .avatar(AvatarHostUtil.settingAvatar(groupContact.getGroupAvatar()))
                        .displayName(groupContact.getName())
                        .index(IM_GROUP_DEFAULT_INDEX)
                        .isGroup(true)
                        .isInvite(groupContact.getGroupInvite()).build();
            } else {
                if (friend == null) {
                    return null;
                }
                AccountProfileStruct struct = profileMap.get(friend.getUserId());
                return ContactVO.builder()
                        .id(friend.getUserId().toString())
                        .displayName(StringUtils.isBlank(friend.getRemark()) ? struct.nickname : friend.getRemark())
                        .avatar(struct.avatar)
                        .isGroup(false)
                        .index(friend.getIndex()).build();
            }
        }).filter(Objects::nonNull).toList();
    }

    private List<ConversationVO> buildConversations(Long userId, Map<Long, AccountProfileStruct> profileMap, List<ChatDTO> chats) {
        if (CollectionUtils.isEmpty(chats)) {
            return Collections.emptyList();
        }
        List<ConversationVO> vos = chats.parallelStream()
                // 过滤 会话被移除（Remove != null || 最后一条消息时间 小于 移除时间）的数据
                .filter(chat -> chat.getConversation() != null
                        && (chat.getConversation().getLastRemoveTime() == null ||
                        chat.getConversation().getLastRemoveTime() < chat.getConversation().getLastMessageTime().getTime()))
                .map(chat -> {
                    ImConversation conversation = chat.getConversation();
                    Long contactId = conversation.getContactId();
                    Boolean group = conversation.getGroup();
                    if (group) {
                        GroupContactDTO groupContact = chat.getGroupContact();
                        if (groupContact == null) {
                            return null;
                        }
                        return buildGroupConversationVO(conversation, contactId, groupContact, profileMap);
                    } else {
                        AccountProfileStruct struct = profileMap.get(contactId);
                        if (struct == null) {
                            return null;
                        }
                        ImFriend friend = chat.getFriend();
                        return buildPrivateChatConversationVO(friend == null ? null : friend.getRemark(), struct, conversation, null);
                    }
                }).filter(Objects::nonNull).toList();

        //获取所有会话列表的未读消息
        Map<String, Integer> unreadMap = messageOperationsService.getConversationUnread(userId, vos.parallelStream().filter(ConversationVO::getIsNotice).map(vo -> MessageUnreadDTO.builder()
                .conversationId(Long.parseLong(vo.getConversationId()))
                .userId(userId)
                .toContactId(Long.parseLong(vo.getId()))
                .isGroup(vo.getIsGroup()).build()).collect(Collectors.toList()));
        // 设置会话列表未读消息 并且排序
        return vos.parallelStream().peek(vo -> vo.setUnread(unreadMap.getOrDefault(vo.getConversationId(), 0)))
                .toList();
    }



    private Set<Long> getAllUserIdsByChats(Long userId, List<ChatDTO> chats) {
        Set<Long> allUserIds = new HashSet<>();
        for (ChatDTO chat : chats) {
            if (chat.getConversation() != null) {
                allUserIds.add(chat.getConversation().getContactId());
            }
            if (chat.getFriend() != null) {
                allUserIds.add(chat.getFriend().getUserId());
            }
        }
        allUserIds.add(userId);
        return allUserIds;
    }

    @Override
    public boolean updateGroupChatTopStatus(Long id, Long groupId, Boolean status) {
        ImConversation conversation = ImConversation.of(id, groupId, true);
        conversation = conversationTkService.queryOne(conversation);
        if (conversation == null) {
            return false;
        }
        conversation.setTop(status);
        ImGroupMember member = ImGroupMember.of(groupId, id);
        member.setTop(status);
        //update db.
        ImConversation finalConversation = conversation;
        Boolean execute = template.execute(transactionStatus -> {
            try {
                AssertUtil.isTrue(conversationTkService.update(finalConversation), "Failed execute to update conversation top status.");
                AssertUtil.isTrue(imGroupMemberTkService.updateMember(member), "Failed execute to update group member top status.");
                return true;
            } catch (Throwable cause) {
                transactionStatus.setRollbackOnly();
                return false;
            }
        });
        if (Boolean.TRUE.equals(execute)) {
            sendTopChatEvent(conversation);
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePrivateChatTopStatus(Long id, Long contactId, Boolean status) {
        ImConversation conversation = ImConversation.of(id, contactId, false);
        conversation = conversationTkService.queryOne(conversation);
        if (conversation == null) {
            return false;
        }
        conversation.setTop(status);
        ImFriend imFriend = ImFriend.of(id, contactId);
        imFriend.setTop(status);
        //update db.
        ImConversation finalConversation = conversation;
        Boolean execute = template.execute(transactionStatus -> {
            try {
                AssertUtil.isTrue(conversationTkService.update(finalConversation), "Failed execute to update conversation top status.");
                AssertUtil.isTrue(imFriendTkService.updateImFriend(imFriend), "Failed execute to update friend top status.");
                return true;
            } catch (Throwable cause) {
                transactionStatus.setRollbackOnly();
                return false;
            }
        });
        if (Boolean.TRUE.equals(execute)) {
            sendTopChatEvent(conversation);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateGroupChatNoticeStatus(Long id, Long groupId, Boolean status) {
        ImConversation conversation = ImConversation.of(id, groupId, true);
        conversation = conversationTkService.queryOne(conversation);
        if (conversation == null) {
            return false;
        }
        conversation.setNotice(status);
        ImGroupMember member = ImGroupMember.of(groupId, id);
        member.setNotice(status);
        //update db.
        ImConversation finalConversation = conversation;
        Boolean execute = template.execute(transactionStatus -> {
            try {
                AssertUtil.isTrue(conversationTkService.update(finalConversation), "Failed execute to update conversation notice status.");
                AssertUtil.isTrue(imGroupMemberTkService.updateMember(member), "Failed execute to update group member notice status.");
                return true;
            } catch (Throwable cause) {
                log.error(cause.getMessage());
                transactionStatus.setRollbackOnly();
                return false;
            }
        });
        if (Boolean.TRUE.equals(execute)) {
            sendNoticeChatEvent(conversation);
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePrivateChatNoticeStatus(Long id, Long contactId, Boolean status) {
        ImConversation conversation = ImConversation.of(id, contactId, false);
        conversation = conversationTkService.queryOne(conversation);
        if (conversation == null) {
            return false;
        }
        conversation.setNotice(status);
        ImFriend imFriend = ImFriend.of(id, contactId);
        imFriend.setNotice(status);
        //update db.
        ImConversation finalConversation = conversation;
        Boolean execute = template.execute(transactionStatus -> {
            try {
                AssertUtil.isTrue(conversationTkService.update(finalConversation), "Failed execute to update conversation notice status.");
                AssertUtil.isTrue(imFriendTkService.updateImFriend(imFriend), "Failed execute to update friend notice status.");
                return true;
            } catch (Throwable cause) {
                log.error(cause.getMessage());
                transactionStatus.setRollbackOnly();
                return false;
            }
        });
        if (Boolean.TRUE.equals(execute)) {
            sendNoticeChatEvent(conversation);
            return true;
        }
        return false;
    }

    @Override
    public boolean sendAppendPrivateChatEvent(ImConversation imConversation, Integer unread) {
        Long userId = imConversation.getUserId();
        Long contactId = imConversation.getContactId();
        AccountProfileStruct profile = AccountRpcUtil.getAccountProfile(contactId);
        if (profile == null || profile.getId() == null) {
            log.warn("Failed execute to send append private chat contact, because not found user profile, contactId: {}.", contactId);
            return false;
        }
        //query remark.
        Map<Long, String> map = friendOperationsService.getFriendRemarks(userId, Collections.singletonList(contactId));
        String remark = MapUtil.isNotEmpty(map) ? map.get(contactId) : StrUtil.EMPTY;
        //build conversation vo
        ConversationVO conversation = buildPrivateChatConversationVO(remark, profile, imConversation, unread);
        //build contact vo
        ContactVO contact = ContactVO.builder()
                .id(contactId.toString())
                .index(ConvertUtil.getIndex(StringUtils.isBlank(remark) ? profile.nickname : remark))
                .displayName(StringUtils.isBlank(remark) ? profile.nickname : remark)
                .avatar(profile.avatar)
                .isTop(conversation.getIsTop())
                .isNotice(conversation.getIsNotice()).build();
        AppendChatEvent chatEvent = AppendChatEvent.of(userId.toString(), conversation, contact);
        return imEventListener.onImAppendPrivateChatEvent(chatEvent);
    }


    @Override
    public ConversationVO addConversation(Long id, Long userId) {
        AccountProfileStruct accountProfile = AccountRpcUtil.getAccountProfile(userId);
        if (accountProfile == null) {
            return null;
        }
        ImConversation conversation = conversationTkService.queryOne(ImConversation.of(id, userId, false));
        if (conversation == null) {
            conversation = ImConversation.ofDefault(id, userId, false);
            if (!conversationTkService.insert(conversation)) {
                return null;
            }
        } else {
            Date lastMessageTime = conversation.getLastMessageTime();
            Long lastRemoveTime = conversation.getLastRemoveTime();
            if (lastMessageTime != null && lastRemoveTime != null && (lastMessageTime.getTime() > lastRemoveTime)) {
              // 不需要更新会话.
            } else {
                //说明会话之前被移除过,再次重新添加 更新最后一条消息内容
                conversation.setLastMessageTime(new Date());
                conversation.setLastMessageContent(StrUtil.EMPTY);
                if (!conversationTkService.update(conversation)) {
                    return null;
                }
            }
        }
        return buildPrivateChatConversationVO(null, accountProfile, conversation, 0);
    }

    @Override
    public boolean deleteConversation(ImConversation conversation) {
        Long id = conversation.getId();
        boolean deleted = conversation.getDeleted() != null;
        boolean result;
        if (deleted) {
            result = Boolean.TRUE.equals(template.execute(status -> {
                try {
                    if (conversation.getGroup()) {
                        imGroupMemberTkService.delete(ImGroupMember.of(conversation.getContactId(), conversation.getUserId()));
                    }
                    AssertUtil.isTrue(conversationTkService.deleteByPrimaryKey(id), "Failed execute to delete conversation.");
                    return true;
                } catch (Throwable cause) {
                    log.error(cause.getMessage(), cause);
                    status.setRollbackOnly();
                    return false;
                }
            }));
        } else {
            conversation.setLastRemoveTime(System.currentTimeMillis());
            result = conversationTkService.update(conversation);
        }
        return result;
    }

    private void sendTopChatEvent(ImConversation conversation) {
        ImTopChatEvent event = ImTopChatEvent.of(conversation.getUserId().toString(), conversation.getContactId().toString(),
                conversation.getId().toString(), conversation.getTop());
        imEventListener.onImTopChatEvent(event);
    }

    private void sendNoticeChatEvent(ImConversation conversation) {
        ImNoticeChatEvent event = ImNoticeChatEvent.of(conversation.getUserId().toString(), conversation.getContactId().toString(),
                conversation.getId().toString(), conversation.getNotice());
        imEventListener.onImNoticeChatEvent(event);
    }

    private List<ConversationVO> convert(final Long id, final List<ImConversation> conversations, boolean isGroup) {
        if (CollectionUtils.isEmpty(conversations)) {
            return Collections.emptyList();
        }
        List<Long> ids = conversations.parallelStream().map(ImConversation::getContactId).toList();
        if (isGroup) {
            List<GroupMemberDTO> groupMembers = imGroupMemberTkService.queryMembers(id, ids);
            if (CollectionUtils.isEmpty(groupMembers)) {
                return Collections.emptyList();
            }
            Map<Long, GroupMemberDTO> map = groupMembers.parallelStream().collect(Collectors.toMap(GroupMemberDTO::getGroupId, g -> g));
            return conversations.parallelStream().map(conversation -> {
                Long contactId = conversation.getContactId();
                GroupMemberDTO member = map.get(contactId);
                if (member == null) {
                    return null;
                }
                return ConversationVO.builder()
                        .id(contactId.toString())
                        .conversationId(conversation.getId().toString())
                        .displayName(member.getGroupName())
                        .avatar(AvatarHostUtil.settingAvatar(member.getGroupAvatar()))
                        .isGroup(true)
                        .isNotice(conversation.getNotice())
                        .isTop(conversation.getTop())
                        .role(member.getRole())
                        .invite(member.getGroupInvite())
                        .creator(member.getGroupCreator().toString())
                        .type(conversation.getLastMessageType())
                        .lastSendTime(conversation.getLastMessageTime().getTime())
                        .lastContent(conversation.getLastMessageContent()).build();
            }).collect(Collectors.toList());

        } else {
            Map<Long, String> friendRemarks = friendOperationsService.getFriendRemarks(id, ids);
            Map<Long, AccountProfileStruct> infoStructMap = AccountRpcUtil.getAccountProfileMap(ids);
            return conversations.parallelStream().map(conversation -> {
                Long contactId = conversation.getContactId();
                AccountProfileStruct struct = infoStructMap.get(contactId);
                if (struct == null) {
                    return null;
                }
                String remark = friendRemarks.get(contactId);
                return buildPrivateChatConversationVO(remark, struct, conversation, null);
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }

    }

    private ConversationVO buildGroupConversationVO(ImConversation conversation, Long contactId, GroupContactDTO groupContact, Map<Long, AccountProfileStruct> profileMap) {
        return ConversationVO.builder()
                .id(contactId.toString())
                .conversationId(conversation.getId().toString())
                .displayName(groupContact.getName())
                .avatar(AvatarHostUtil.settingAvatar(groupContact.getGroupAvatar()))
                .role(conversation.getDeleted() != null ? GroupRole.REMOVED.role : groupContact.getRole())
                .isGroup(true)
                .creator(groupContact.getCreator().toString())
                .creatorName(profileMap.get(groupContact.getCreator()).nickname)
                .notice(conversation.getDeleted() != null ? null : groupContact.getGroupNotice())
                .isNotice(conversation.getNotice())
                .isTop(conversation.getTop())
                .invite(groupContact.getGroupInvite())
                .type(conversation.getLastMessageType())
                .lastSendTime(conversation.getLastMessageTime() == null ? conversation.getCreated().getTime() : conversation.getLastMessageTime().getTime())
                .lastContent(conversation.getLastMessageContent()).build();
    }


    private ConversationVO buildPrivateChatConversationVO(String remark, AccountProfileStruct struct, ImConversation conversation, Integer unread) {
        return ConversationVO.builder()
                .id(conversation.getContactId().toString())
                .conversationId(conversation.getId().toString())
                .displayName(StringUtils.isBlank(remark) ? struct.nickname : remark)
                .avatar(struct.avatar)
                .isGroup(false)
                .unread(unread)
                .isNotice(conversation.getNotice())
                .isTop(conversation.getTop())
                .type(conversation.getLastMessageType())
                .lastSendTime(conversation.getLastMessageTime() == null ? conversation.getCreated().getTime() : conversation.getLastMessageTime().getTime())
                .lastContent(conversation.getLastMessageContent()).build();
    }


}
