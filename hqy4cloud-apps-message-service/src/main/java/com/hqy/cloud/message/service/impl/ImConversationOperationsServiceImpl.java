package com.hqy.cloud.message.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.bind.event.support.AppendChatEvent;
import com.hqy.cloud.message.bind.event.support.ImNoticeChatEvent;
import com.hqy.cloud.message.bind.event.support.ImTopChatEvent;
import com.hqy.cloud.message.bind.vo.ContactVO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public List<ConversationVO> getImConversations(Long id) {
        ImConversation of = ImConversation.of(id);
        of.setRemove(false);
        List<ImConversation> conversations = conversationTkService.queryList(of);
        if (CollectionUtils.isEmpty(conversations)) {
            return Collections.emptyList();
        }
        Map<Boolean, List<ImConversation>> map = conversations.parallelStream().collect(Collectors.groupingBy(ImConversation::getGroup));
        //好友会话列表
        List<ImConversation> friendConversations = map.get(Boolean.FALSE);
        List<ConversationVO> friendConversationVos = convert(id, friendConversations, false);
        //群聊会话列表
        List<ImConversation> groupConversations = map.get(Boolean.TRUE);
        List<ConversationVO> groupConversationVos = convert(id, groupConversations, true);
        friendConversationVos.addAll(groupConversationVos);
        //所有会话列表
        List<ConversationVO> all = friendConversationVos;
        //获取所有会话列表的未读消息
        Map<String, Integer> unreadMap = messageOperationsService.getConversationUnread(id, all.parallelStream().filter(ConversationVO::getIsNotice).map(vo -> MessageUnreadDTO.builder()
                .conversationId(Long.parseLong(vo.getConversationId()))
                .from(Long.parseLong(vo.getId()))
                .to(id)
                .isGroup(vo.getIsGroup()).build()).collect(Collectors.toList()));
        // sort by message and setting unread.
        all = all.parallelStream().peek(vo -> vo.setUnread(unreadMap.getOrDefault(vo.getConversationId(), 0)))
                .sorted((v1, v2) -> {
                    if (v1.getIsTop().equals(v2.getIsTop())) {
                        return (int)(v2.getLastSendTime() - v1.getLastSendTime());
                    }
                    return v1.getIsTop() ? 1 : -1;
                })
                .collect(Collectors.toList());
        return all;
    }

    @Override
    public boolean updateGroupChatTopStatus(Long id, Long groupId, Boolean status) {
        ImConversation conversation = ImConversation.of(id, groupId, true);
        conversation = conversationTkService.queryOne(conversation);
        if (conversation == null) {
            return false;
        }
        conversation.setTop(status);
        conversation.setLastMessageFrom(true);
        ImGroupMember member = ImGroupMember.of(groupId, id);
        member.setTop(status);
        //update db.
        ImConversation finalConversation = conversation;
        Boolean execute = template.execute(transactionStatus -> {
            try {
                AssertUtil.isTrue(conversationTkService.update(finalConversation), "Failed execute to update conversation top status.");
                AssertUtil.isTrue(imGroupMemberTkService.updateSelective(member), "Failed execute to update group member top status.");
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
        conversation.setLastMessageFrom(true);
        //update db.
        ImConversation finalConversation = conversation;
        Boolean execute = template.execute(transactionStatus -> {
            try {
                AssertUtil.isTrue(conversationTkService.update(finalConversation), "Failed execute to update conversation top status.");
                AssertUtil.isTrue(imFriendTkService.updateSelective(imFriend), "Failed execute to update friend top status.");
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
        conversation.setLastMessageFrom(true);
        ImGroupMember member = ImGroupMember.of(groupId, id);
        member.setNotice(status);
        //update db.
        ImConversation finalConversation = conversation;
        Boolean execute = template.execute(transactionStatus -> {
            try {
                AssertUtil.isTrue(conversationTkService.update(finalConversation), "Failed execute to update conversation notice status.");
                AssertUtil.isTrue(imGroupMemberTkService.updateSelective(member), "Failed execute to update group member notice status.");
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
        conversation.setLastMessageFrom(true);
        ImFriend imFriend = ImFriend.of(id, contactId);
        imFriend.setNotice(status);
        //update db.
        ImConversation finalConversation = conversation;
        Boolean execute = template.execute(transactionStatus -> {
            try {
                AssertUtil.isTrue(conversationTkService.update(finalConversation), "Failed execute to update conversation notice status.");
                AssertUtil.isTrue(imFriendTkService.updateSelective(imFriend), "Failed execute to update friend notice status.");
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
    public boolean sendAppendPrivateChatEvent(ImConversation imConversation) {
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
        ConversationVO conversation = buildPrivateChatConversationVO(remark, profile, imConversation);
        //build contact vo
        char fistChar = StringUtils.isBlank(remark) ? profile.nickname.charAt(0) : remark.charAt(0);
        ContactVO contact = ContactVO.builder()
                .id(contactId.toString())
                .index(PinyinUtil.getFirstLetter(fistChar) + "")
                .displayName(StringUtils.isBlank(remark) ? profile.nickname : remark)
                .avatar(profile.avatar)
                .isTop(conversation.getIsTop())
                .isNotice(conversation.getIsNotice()).build();
        AppendChatEvent chatEvent = AppendChatEvent.of(false, Collections.singletonList(userId.toString()), conversation, contact);
        return imEventListener.onImAppendChatEvent(chatEvent);
    }

    private void sendTopChatEvent(ImConversation conversation) {
        ImTopChatEvent event = ImTopChatEvent.of(conversation.getUserId().toString(), conversation.getContactId().toString(),
                conversation.getId().toString(), conversation.getTop());
        imEventListener.onImTopChatEvent(event);
    }

    private void sendNoticeChatEvent(ImConversation conversation) {
        ImNoticeChatEvent event = ImNoticeChatEvent.of(conversation.getUserId().toString(), conversation.getContactId().toString(),
                conversation.getId().toString(), conversation.getTop());
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
                        .avatar(member.getGroupAvatar())
                        .isGroup(true)
                        .isRemove(conversation.getRemove())
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
                return buildPrivateChatConversationVO(remark, struct, conversation);
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }

    }

    private ConversationVO buildPrivateChatConversationVO(String remark, AccountProfileStruct struct, ImConversation conversation) {
        return ConversationVO.builder()
                .id(conversation.getContactId().toString())
                .conversationId(conversation.getId().toString())
                .displayName(StringUtils.isBlank(remark) ? struct.nickname : remark)
                .avatar(struct.avatar)
                .isGroup(false)
                .isRemove(conversation.getRemove())
                .isNotice(conversation.getNotice())
                .isTop(conversation.getTop())
                .type(conversation.getLastMessageType())
                .lastSendTime(conversation.getLastMessageTime().getTime())
                .lastContent(conversation.getLastMessageContent()).build();
    }


}
