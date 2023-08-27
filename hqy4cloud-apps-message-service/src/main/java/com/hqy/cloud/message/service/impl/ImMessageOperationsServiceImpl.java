package com.hqy.cloud.message.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.id.DistributedIdGen;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.bind.event.support.PrivateChatEvent;
import com.hqy.cloud.message.bind.event.support.ReadMessagesEvent;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
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
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_MESSAGE_FAILED;
import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.IM_MESSAGE_SUCCESS;
import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.UNION;

/**
 * @author qiyuan.hong
 * @date 2023-08-20 21:41
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImMessageOperationsServiceImpl implements ImMessageOperationsService {
    private static final String UNREAD_KEY = MicroServiceConstants.MESSAGE_NETTY_SERVICE + UNION + "IM_UNREAD";

    private final TransactionTemplate template;
    private final ImEventListener eventListener;
    private final ImConversationTkService conversationTkService;
    private final ImMessageTkService messageTkService;
    private final ImMessageElasticService imMessageElasticService;

    private final RedissonClient redissonClient;
    private final Map<String, RMapCache<String, Integer>> privateUnreadConversationCache = MapUtil.newConcurrentHashMap();


    @Override
    public Map<String, Integer> getConversationUnread(Long id, List<MessageUnreadDTO> messageUnreadList) {
        if (CollectionUtils.isEmpty(messageUnreadList)) {
            return MapUtil.newHashMap();
        }
        final String key = genUnreadKey(id);
        RMapCache<String, Integer> cache = this.privateUnreadConversationCache.computeIfAbsent(key, k -> redissonClient.getMapCache(key));
        if (cache.isEmpty()) {
            messageUnreadList = messageTkService.queryUnread(id, messageUnreadList);
            Map<String, Integer> map = messageUnreadList.stream().collect(Collectors.toMap(k -> k.getConversationId().toString(), MessageUnreadDTO::getUnread));
            cache.putAll(map);
        }
        return cache;
    }

    @Override
    public void increaseConversationUnread(Long id, Long conversationId) {
        String key = genUnreadKey(id);
        RMapCache<String, Integer> cache = this.privateUnreadConversationCache.computeIfAbsent(key, k -> redissonClient.getMapCache(key));
        String field = conversationId.toString();
        cache.put(field, cache.getOrDefault(field, 0) + 1);
    }

    @Override
    public void readConversation(Long id, Long conversationId) {
        String key = genUnreadKey(id);
        RMapCache<String, Integer> cache = this.privateUnreadConversationCache.computeIfAbsent(key, k -> redissonClient.getMapCache(key));
        String field = conversationId.toString();
        cache.put(field, 0);
    }

    @Override
    public void removeConversationUnread(Long id, Long conversationId) {
        String key = genUnreadKey(id);
        RMapCache<String, Integer> cache = this.privateUnreadConversationCache.computeIfAbsent(key, k -> redissonClient.getMapCache(key));
        String field = conversationId.toString();
        cache.remove(field);
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
            boolean result = false;
            String eventName = "";
            if (im.getGroup()) {
                //TODO
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
            this.readConversation(conversation.getUserId(), conversation.getId());
            // send read messages event.
            ReadMessagesEvent event = new ReadMessagesEvent(conversation.getContactId().toString(), ids);
            eventListener.onReadMessages(event);
            return ids;
        }
        return Collections.emptyList();
    }

    private List<ImConversation> buildConversations(Long id, ImMessageDTO message) {
        Long to = Long.valueOf(message.getToContactId());
        Date date = new Date();
        //from conversations
        ImConversation fromConversation = getImConversation(id, to, message, date);
        //to conversations
        ImConversation toConversations = getImConversation(to, id, message, date);
        return Arrays.asList(fromConversation, toConversations);
    }

    @NotNull
    private ImConversation getImConversation(Long id, Long to, ImMessageDTO message, Date date) {
        ImConversation conversation = new ImConversation(id, to, message.getIsGroup());
        conversation.setLastMessageContent(message.getContent());
        conversation.setLastMessageType(message.getType());
        conversation.setLastMessageTime(new Date(message.getSendTime()));
        conversation.setCreated(date);
        conversation.setUpdated(date);
        conversation.setLastMessageFrom(id.toString().equals(message.getFromUser().getId()));
        return conversation;
    }

    private String genUnreadKey(Long id) {
        return UNREAD_KEY + UNION + id;
    }

    private String genUnreadGroupKey(Long userId, Long groupId) {
        return UNREAD_KEY + groupId + userId;
    }



}
