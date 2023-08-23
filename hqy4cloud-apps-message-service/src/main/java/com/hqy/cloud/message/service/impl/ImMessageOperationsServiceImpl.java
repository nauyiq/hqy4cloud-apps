package com.hqy.cloud.message.service.impl;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.service.ImMessageOperationsService;
import com.hqy.cloud.message.tk.service.ImMessageTkService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.UNION;

/**
 * @author qiyuan.hong
 * @date 2023-08-20 21:41
 */
@Service
@RequiredArgsConstructor
public class ImMessageOperationsServiceImpl implements ImMessageOperationsService {
    private static final String UNREAD_KEY = MicroServiceConstants.MESSAGE_NETTY_SERVICE + UNION + "IM_FRIEND_UNREAD";
    private final RedissonClient redissonClient;
    private final ImMessageTkService messageTkService;
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


    private String genUnreadKey(Long id) {
        return UNREAD_KEY + UNION + id;
    }
}
