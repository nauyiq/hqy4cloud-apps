package com.hqy.cloud.message.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.apps.commom.constants.AppsConstants;
import com.hqy.cloud.foundation.id.DistributedIdGen;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.bind.event.support.GroupChatEvent;
import com.hqy.cloud.message.bind.event.support.PrivateChatEvent;
import com.hqy.cloud.message.bind.event.support.ReadMessagesEvent;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.cache.ImUnreadCacheService;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.es.document.ImMessageDoc;
import com.hqy.cloud.message.es.service.ImMessageElasticService;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.service.ImMessageOperationsService;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.entity.ImMessage;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImMessageTkService;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_MESSAGE_FAILED;
import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_MESSAGE_SUCCESS;

/**
 * ImMessageOperationsService.
 * @author qiyuan.hong
 * @date 2023-08-20 21:41
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImMessageOperationsServiceImpl implements ImMessageOperationsService {
    private final TransactionTemplate template;
    private final ImEventListener eventListener;
    private final ImConversationTkService conversationTkService;
    private final ImMessageTkService messageTkService;
    private final ImMessageElasticService imMessageElasticService;
    private final ImUnreadCacheService imUnreadCacheService;

    @Override
    public Map<String, Integer> getConversationUnread(Long id, List<MessageUnreadDTO> messageUnreadList) {
        if (CollectionUtils.isEmpty(messageUnreadList)) {
            return MapUtil.newHashMap();
        }
        Map<Boolean, List<MessageUnreadDTO>> map = messageUnreadList.stream().collect(Collectors.groupingBy(MessageUnreadDTO::getIsGroup));
        List<MessageUnreadDTO> groupUnreadList = map.get(Boolean.TRUE);
        List<MessageUnreadDTO> privateUnreadList = map.get(Boolean.FALSE);

        Map<String, Integer> resultMap = MapUtil.newHashMap(messageUnreadList.size());
        // search group unread result.
        if (CollectionUtils.isNotEmpty(groupUnreadList)) {
            List<Long> groupIds = groupUnreadList.stream().map(MessageUnreadDTO::getFrom).toList();
            Map<Long, Integer> unread = imUnreadCacheService.groupConversationsUnread(id, groupIds);
            groupUnreadList.forEach(groupDTO -> {
                Integer groupUnread = unread.getOrDefault(groupDTO.getFrom(), 0);
                resultMap.put(groupDTO.getConversationId().toString(), groupUnread);
            });
        }
        // search private unread result.
        if (CollectionUtils.isNotEmpty(privateUnreadList)) {
            List<Long> privateIds = privateUnreadList.stream().map(MessageUnreadDTO::getConversationId).toList();
            Map<Long, Integer> unread = imUnreadCacheService.privateConversationsUnread(id, privateIds);
            unread.keySet().forEach(k -> resultMap.put(k.toString(), unread.getOrDefault(k, 0)));
        }
        return resultMap;
    }

    @Override
    public ImMessageVO sendImMessage(Long id, ImMessageDTO message) {
        List<ImConversation> imConversations = buildConversations(id, message);
        ImMessage im = template.execute(status -> {
            try {
                // insert or update conversations.
                AssertUtil.isTrue(conversationTkService.insertOrUpdate(imConversations), "Failed execute to insert or update conversations by send im message.");
                // insert message
                ImMessage imMessage = ImMessage.of(DistributedIdGen.getSnowflakeId(), id, message);
                AssertUtil.isTrue(messageTkService.insert(imMessage), "Failed execute to insert message bt send im message.");
                // insert message doc to es.
                ImMessageDoc messageDoc = new ImMessageDoc(imMessage);
                imMessageElasticService.save(messageDoc);
                return imMessage;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.warn(cause.getMessage());
                return null;
            }
        });
        String status = im == null ? IM_MESSAGE_FAILED : IM_MESSAGE_SUCCESS;
        message.setStatus(status);
        message.setIsRead(false);
        message.setMessageId(im == null ? StrUtil.EMPTY : im.getId().toString());
        if (im != null) {
            // send socket message.
            boolean result;
            String eventName;
            if (im.getGroup()) {
                //send groupChat event.
                Set<String> members = imConversations.stream().map(groupIm -> groupIm.getUserId().toString()).filter(userId -> !userId.equals(id.toString())).collect(Collectors.toSet());
                GroupChatEvent event = new GroupChatEvent(members, message);
                eventName = event.name();
                result = eventListener.onGroupChat(event);
            } else {
                //send privateChat event.
                PrivateChatEvent event = new PrivateChatEvent(message);
                eventName = event.name();
                result = eventListener.onPrivateChat(event);
            }
            log.info("Send im message end, name: {}, result: {}.", eventName, result);
        }
        return message;
    }


    @Override
    public List<String> readMessages(ImConversation conversation) {
        List<ImMessageDoc> unreadMessages = imMessageElasticService.queryUnreadMessages(conversation.getContactId(), conversation.getUserId());
        if (CollectionUtils.isEmpty(unreadMessages)) {
            // Not found unread messages.
            if (log.isDebugEnabled()) {
                log.debug("Conversation not found unread messages by id: {}.", conversation.getId());
            }
            return Collections.emptyList();
        }

        List<Long> unreadMessageIds = unreadMessages.parallelStream().map(ImMessageDoc::getId).toList();
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(messageTkService.updateMessagesRead(unreadMessageIds), "Failed execute to update db message unread.");
                List<ImMessageDoc> docs = unreadMessages.parallelStream().peek(u -> u.setRead(true)).toList();
                imMessageElasticService.saveAll(docs);
                return true;
            } catch (Throwable cause){
                status.setRollbackOnly();
                return false;
            }
        });
        if (Boolean.TRUE.equals(execute)) {
            List<String> ids = unreadMessageIds.parallelStream().map(Object::toString).toList();
            // remove redis conversation unread.
            if (conversation.getGroup()) {
                imUnreadCacheService.readGroupConversationUnread(conversation.getUserId(), conversation.getContactId());
            } else {
                imUnreadCacheService.readPrivateConversationUnread(conversation.getUserId(), conversation.getId());
            }
            // send read messages event.
            ReadMessagesEvent event = new ReadMessagesEvent(conversation.getContactId().toString(), ids);
            eventListener.onReadMessages(event);
            return ids;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean undoMessage(ImMessage imMessage) {
        ImConversation conversation = ImConversation.of(imMessage.getFrom(), imMessage.getTo(), imMessage.getGroup());
        // update undo entity.
        conversation.setLastMessageFrom(true);
        conversation.setLastMessageType(ImMessageType.EVENT.type);
        conversation.setLastMessageContent(AppsConstants.Message.UNDO_FROM_MESSAGE_CONTENT);
        conversation.setLastMessageTime(imMessage.getCreated());
        imMessage.setContent(AppsConstants.Message.UNDO_FROM_MESSAGE_CONTENT);
        imMessage.setType(ImMessageType.TEXT.type);
        Boolean execute = template.execute(status -> {
            try {
                AssertUtil.isTrue(conversationTkService.updateSelective(conversation), "Failed execute to update conversation.");
                AssertUtil.isTrue(messageTkService.updateSelective(imMessage), "Failed execute to update im message.");
                imMessageElasticService.save(new ImMessageDoc(imMessage));
                return true;
            } catch (Throwable cause) {
                log.error(cause.getMessage());
                status.setRollbackOnly();
                return false;
            }
        });
        if (Boolean.TRUE.equals(execute)) {
            return true;
        }
        return false;
    }

    private List<ImConversation> buildConversations(Long id, ImMessageDTO message) {
        Long to = Long.valueOf(message.getToContactId());
        Date date = new Date();
        if (message.getIsGroup()) {
            // build group conversations.
            // search group members.
            List<ImConversation> groupConversations = conversationTkService.queryGroupConversationMembers(to);
            AssertUtil.notEmpty(groupConversations, "Group members should not be empty.");
            return groupConversations.stream().map(conversation -> buildSendMessageConversation(conversation.getUserId(), to, message, date, conversation.getId())).toList();
        } else {
            // build private conversations.
            ImConversation fromConversation = buildSendMessageConversation(id, to, message, date, null);
            ImConversation toConversations = buildSendMessageConversation(to, id, message, date, null);
            return Arrays.asList(fromConversation, toConversations);
        }
    }

    private ImConversation buildSendMessageConversation(Long id, Long to, ImMessageDTO message, Date date, Long conversationId) {
        ImConversation conversation = new ImConversation(id, to, message.getIsGroup());
        conversation.setId(conversationId);
        conversation.setGroup(message.getIsGroup());
        conversation.setTop(false);
        conversation.setNotice(true);
        conversation.setRemove(false);
        conversation.setLastMessageFrom(id.toString().equals(message.getFromUser().getId()));
        conversation.setLastMessageContent(message.getContent());
        conversation.setLastMessageType(message.getType());
        conversation.setLastMessageTime(new Date(message.getSendTime()));
        conversation.setCreated(date);
        conversation.setUpdated(date);
        return conversation;
    }


}
