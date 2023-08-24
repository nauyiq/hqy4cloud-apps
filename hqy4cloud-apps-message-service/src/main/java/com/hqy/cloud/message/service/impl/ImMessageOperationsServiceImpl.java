package com.hqy.cloud.message.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.id.DistributedIdGen;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.message.bind.event.support.PrivateChatEvent;
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
    private static final String UNREAD_KEY = MicroServiceConstants.MESSAGE_NETTY_SERVICE + UNION + "IM_FRIEND_UNREAD";
    private final RedissonClient redissonClient;
    private final TransactionTemplate template;
    private final ImEventListener eventListener;
    private final ImConversationTkService conversationTkService;
    private final ImMessageTkService messageTkService;
    private final ImMessageElasticService imMessageElasticService;
    private final Map<String, RMapCache<String, Integer>> cache = MapUtil.newConcurrentHashMap();

    @Override
    public Map<String, Integer> getConversationUnread(Long id, List<MessageUnreadDTO> messageUnreadList) {
        if (CollectionUtils.isEmpty(messageUnreadList)) {
            return MapUtil.newHashMap();
        }
        final String key = genUnreadKey(id);
        RMapCache<String, Integer> cache = this.cache.computeIfAbsent(key, k -> redissonClient.getMapCache(key));
        if (cache.isEmpty()) {
            messageUnreadList = messageTkService.queryUnread(id, messageUnreadList);
            Map<String, Integer> map = messageUnreadList.stream().collect(Collectors.toMap(k -> k.getConversationId().toString(), MessageUnreadDTO::getUnread));
            cache.putAll(map);
        }
        return cache;
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
                //update conversation unread count.
                Long conversationId = imConversations.stream().filter(i -> i.getUserId().equals(Long.valueOf(message.getToContactId()))).map(ImConversation::getId).toList().get(0);

                //send socket.io event.
                PrivateChatEvent event = new PrivateChatEvent(message);
                eventName = event.name();
                result = eventListener.onPrivateChat(event);
            }
            log.info("Send im message end, name: {}, result: {}.", eventName, result);
        }
        return message;
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
        ImConversation fromConversation = new ImConversation(id, to, message.getIsGroup());
        fromConversation.setLastMessageContent(message.getContent());
        fromConversation.setLastMessageType(message.getType());
        fromConversation.setLastMessageTime(new Date(message.getSendTime()));
        fromConversation.setCreated(date);
        fromConversation.setUpdated(date);
        return fromConversation;
    }

    private String genUnreadKey(Long id) {
        return UNREAD_KEY + UNION + id;
    }
}
