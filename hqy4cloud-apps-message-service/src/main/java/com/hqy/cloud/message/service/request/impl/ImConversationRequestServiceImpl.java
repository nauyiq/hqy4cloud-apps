package com.hqy.cloud.message.service.request.impl;

import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.GroupMemberDTO;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.service.ImFriendOperationsService;
import com.hqy.cloud.message.service.ImMessageOperationsService;
import com.hqy.cloud.message.service.request.ImConversationRequestService;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImGroupMemberTkService;
import com.hqy.cloud.web.common.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 16:40
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImConversationRequestServiceImpl implements ImConversationRequestService {
    private final ImConversationTkService conversationTkService;
    private final ImGroupMemberTkService imGroupMemberTkService;
    private final ImFriendOperationsService friendOperationsService;
    private final ImMessageOperationsService messageOperationsService;

    @Override
    public R<List<ConversationVO>> getConversations(Long id) {
        List<ImConversation> conversations = conversationTkService.queryList(ImConversation.of(id));
        if (CollectionUtils.isEmpty(conversations)) {
            return R.ok(Collections.emptyList());
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
        all = all.parallelStream().peek(vo -> vo.setUnread(unreadMap.getOrDefault(vo.getConversationId(), 0))).collect(Collectors.toList());
        return R.ok(all);
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
            Map<String, String> friendRemarks = friendOperationsService.getFriendRemarks(id);
            Map<Long, AccountBaseInfoStruct> infoStructMap = AccountRpcUtil.getAccountBaseInfoMap(ids);
            return conversations.parallelStream().map(conversation -> {
                Long contactId = conversation.getContactId();
                AccountBaseInfoStruct struct = infoStructMap.get(contactId);
                if (struct == null) {
                    return null;
                }
                String remark = friendRemarks.get(contactId.toString());
                return ConversationVO.builder()
                        .id(contactId.toString())
                        .conversationId(conversation.getId().toString())
                        .displayName(StringUtils.isBlank(remark) ? struct.nickname : remark)
                        .avatar(struct.avatar)
                        .isGroup(false)
                        .isNotice(conversation.getNotice())
                        .isTop(conversation.getTop())
                        .type(conversation.getLastMessageType())
                        .lastSendTime(conversation.getLastMessageTime().getTime())
                        .lastContent(conversation.getLastMessageContent()).build();
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }

    }


}
