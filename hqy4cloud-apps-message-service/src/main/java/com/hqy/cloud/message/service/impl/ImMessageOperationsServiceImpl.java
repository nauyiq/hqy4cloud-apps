package com.hqy.cloud.message.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.UUID;
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
import com.hqy.cloud.message.service.ImConversationOperationsService;
import com.hqy.cloud.message.service.ImMessageOperationsService;
import com.hqy.cloud.message.tk.entity.ImConversation;
import com.hqy.cloud.message.tk.entity.ImMessage;
import com.hqy.cloud.message.tk.service.ImConversationTkService;
import com.hqy.cloud.message.tk.service.ImFriendApplicationTkService;
import com.hqy.cloud.message.tk.service.ImMessageTkService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.*;

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
    private final ImFriendApplicationTkService friendApplicationTkService;
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
    public int getSystemMessageUnread(Long id) {
        if (id == null) {
            return 0;
        }
        Integer unread = imUnreadCacheService.getPrivateConversationUnread(id, IM_SYSTEM_MESSAGE_UNREAD_ID);
        if (unread == null) {
            // redis为空 读取db.
            unread = friendApplicationTkService.getApplicationUnreadMessages(id);
            imUnreadCacheService.addPrivateConversationUnread(id, IM_SYSTEM_MESSAGE_UNREAD_ID, Convert.toLong(unread));
        }
        return unread;
    }


    @Override
    public ImMessageVO sendImMessage(Long id, ImMessageDTO message) {
        Long to = Long.valueOf(message.getToContactId());
        // 查询双方聊天会话.
        List<ImConversation> imConversations = conversationTkService.queryConversations(id, to, message.getIsGroup());
        if (CollectionUtils.isEmpty(imConversations)) {
            log.warn("Failed execute to send im message, conversation should not be empty. userI: {}.", id);
            return null;
        }
        // 当前用户和对方的会话一定是存在的， size == 1表示对方和自己的会话不存在.
        boolean insert = imConversations.size() == 1 && !message.getIsGroup();
        ImConversation toConversation = insert ? buildSendMessageConversation(id, to, message, new Date()) : null;
        updateConversations(message, imConversations);
        ImMessage im = template.execute(status -> {
            try {
                if (insert) {
                    AssertUtil.isTrue(conversationTkService.insert(toConversation), "Failed execute to toConversation");
                }
                AssertUtil.isTrue(conversationTkService.insertOrUpdate(imConversations), "Failed execute to insert or update conversations by send im message.");
                ImMessage imMessage = ImMessage.of(DistributedIdGen.getSnowflakeId(), id, message);
                AssertUtil.isTrue(messageTkService.insert(imMessage), "Failed execute to insert message bt send im message.");
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
        message.setMessageId(im == null ? StrUtil.EMPTY : im.getId().toString());
        if (im != null) {
            boolean result;
            String eventName;
            if (im.getGroup()) {
                //send groupChat event.
                Set<String> members = imConversations.stream().map(groupIm -> groupIm.getUserId().toString()).filter(userId -> !userId.equals(id.toString())).collect(Collectors.toSet());
                // 未读数 + 1
                imUnreadCacheService.addGroupConversationsUnread(members.parallelStream().map(Long::parseLong).collect(Collectors.toSet()), to, 1L);
                GroupChatEvent event = new GroupChatEvent(members, message);
                eventName = event.name();
                result = eventListener.onGroupChat(event);
            } else {
                // 未读数 + 1
                Long toConversationId = insert ? toConversation.getId() :
                        imConversations.stream().filter(conversation -> conversation.getUserId().equals(to)).toList().get(0).getId();
                imUnreadCacheService.addPrivateConversationUnread(to, toConversationId, 1L);
                if (insert) {
                    // 发送新增联系人事件
                    ImConversationOperationsService service = SpringContextHolder.getBean(ImConversationOperationsService.class);
                    service.sendAppendPrivateChatEvent(toConversation, 1);
                }
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
    public void addSystemMessage(Long send, Long receive, String message, Long conversationId) {
        // insert message
        this.addSimpleMessage(send, receive, false, conversationId, null, ImMessageType.SYSTEM, message, System.currentTimeMillis());
    }

    @Override
    public ImMessage addSimpleMessage(Long send, Long receive, boolean isGroup, Long conversationId, List<Long> groupMembers,
                                 ImMessageType messageType, String message, Long messageTime) {
        // 构建消息体对象
        ImMessage imMessage = new ImMessage(DistributedIdGen.getSnowflakeId(), new Date(messageTime), UUID.fastUUID().toString(), isGroup, send, receive, messageType.type, message);
        ImMessageDoc messageDoc = new ImMessageDoc(imMessage);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NotNull TransactionStatus status) {
                try {
                    AssertUtil.isTrue(messageTkService.insert(imMessage), "Failed execute to insert message bt send im message.");
                    imMessageElasticService.save(messageDoc);
                    if (isGroup && CollectionUtils.isNotEmpty(groupMembers)) {
                        imUnreadCacheService.addGroupConversationsUnread(new HashSet<>(groupMembers), receive, 1L);
                    }
                    if (!isGroup && conversationId != null) {
                        imUnreadCacheService.addPrivateConversationUnread(receive, conversationId, 1L);
                    }
                } catch (Throwable cause) {
                    status.setRollbackOnly();
                    log.error(cause.getMessage(), cause);
                }
            }
        });
        return imMessage;
    }

    @Override
    public List<String> readMessages(ImConversation conversation) {
        if (conversation.getGroup()) {
            // 群聊消息
            imUnreadCacheService.readGroupConversationUnread(conversation.getUserId(), conversation.getContactId());
            return Collections.emptyList();
        }
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

    private void updateConversations(ImMessageDTO message, List<ImConversation> imConversations) {
        Date date = new Date();
        imConversations.forEach(conversation -> {
            conversation.setUpdated(date);
            conversation.setLastMessageContent(message.getContent());
            conversation.setLastMessageType(message.getType());
            conversation.setLastMessageTime(new Date(message.getSendTime()));
        });
    }

    private ImConversation buildSendMessageConversation(Long id, Long to, ImMessageDTO message, Date date) {
        ImConversation conversation = new ImConversation(id, to, message.getIsGroup());
        conversation.setGroup(message.getIsGroup());
        conversation.setTop(false);
        conversation.setNotice(true);
        conversation.setLastMessageContent(message.getContent());
        conversation.setLastMessageType(message.getType());
        conversation.setLastMessageTime(new Date(message.getSendTime()));
        conversation.setCreated(date);
        conversation.setUpdated(date);
        return conversation;
    }


}
