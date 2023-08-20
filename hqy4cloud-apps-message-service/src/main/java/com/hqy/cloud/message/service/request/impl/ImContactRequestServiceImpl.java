package com.hqy.cloud.message.service.request.impl;

import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.service.ImFriendOperationsService;
import com.hqy.cloud.message.service.request.ImContactRequestService;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImFriendTkService;
import com.hqy.cloud.message.tk.service.ImGroupTkService;
import com.hqy.cloud.web.common.AccountRpcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
public class ImContactRequestServiceImpl implements ImContactRequestService {
    private final ImConversationTkService conversationTkService;
    private final ImGroupTkService groupTkService;
    private final ImFriendTkService imFriendTkService;
    private final ImFriendOperationsService friendOperationsService;

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


        return R.ok();
    }

    private List<ConversationVO> convert(final Long id, final List<ImConversation> conversations, boolean isGroup) {
        if (CollectionUtils.isEmpty(conversations)) {
            return Collections.emptyList();
        }

        if (isGroup) {

        } else {
            Map<String, String> friendRemarks = friendOperationsService.getFriendRemarks(id);
            List<Long> friendIds = conversations.parallelStream().map(ImConversation::getContactId).collect(Collectors.toList());
            Map<Long, AccountBaseInfoStruct> infoStructMap = AccountRpcUtil.getAccountBaseInfoMap(friendIds);
            return conversations.parallelStream().map(conversation -> {
                Long contactId = conversation.getContactId();
                AccountBaseInfoStruct struct = infoStructMap.get(contactId);
                if (struct == null) {
                    return null;
                }
                String remark = friendRemarks.get(contactId.toString());
                return ConversationVO.builder()
                        .id(contactId.toString())
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


        return null;
    }


}
