package com.hqy.cloud.message.service.impl;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.cache.redis.support.SmartRedisManager;
import com.hqy.cloud.message.service.ImFriendOperationsService;
import com.hqy.cloud.message.tk.entity.ImFriend;
import com.hqy.cloud.message.tk.entity.ImFriendApplication;
import com.hqy.cloud.message.tk.service.ImFriendTkService;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.UNION;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 14:23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImFriendOperationsServiceImpl implements ImFriendOperationsService {
    private final ImFriendTkService friendTkService;
    private static final String KEY = MicroServiceConstants.MESSAGE_NETTY_SERVICE + UNION + "IM_FRIEND";
    private static final String REMARK_KEY = MicroServiceConstants.MESSAGE_NETTY_SERVICE + UNION + "IM_FRIEND_REMARK";
    private static final String TRUE = "1";
    private static final String FALSE = "0";

    @Override
    public boolean addFriend(ImFriendApplication application) {
        AssertUtil.notNull(application, "ImFriendApplication should not be null.");
        Long from = application.getId();
        Long to = application.getUserId();
        //创建好友实体类, 并且为新增两条记录， 分别都是对方的好友.
        List<ImFriend> imFriends = ImFriend.of(from, to, application.getRemark());
        if (!friendTkService.insertList(imFriends)) {
            return false;
        }
        String key = genKey(from, to);
        SmartRedisManager.getInstance().set(key, TRUE);
        //TODO 异步发送系统消息.
        return true;
    }

    @Override
    public boolean isFriend(Long from, Long to) {
        String fromKey = genKey(from, to);
        String toKey = genKey(to, from);
        String valueFrom = SmartRedisManager.getInstance().get(fromKey);
        if (StringUtils.isNotBlank(valueFrom)) {
            return TRUE.equals(valueFrom);
        }
        String valueTo = SmartRedisManager.getInstance().get(toKey);
        if (StringUtils.isNotBlank(valueTo)) {
            return TRUE.equals(valueTo);
        }

        //从数据库中查询好友关系
        ImFriend friend = ImFriend.of(from, to, true);
        friend = friendTkService.queryOne(friend);
        if (friend == null) {
            SmartRedisManager.getInstance().set(fromKey, FALSE);
            return false;
        } else {
            SmartRedisManager.getInstance().set(fromKey, TRUE);
            return true;
        }
    }

    @Override
    public boolean removeFriend(Long from, Long to) {
        ImFriend fromFriend = ImFriend.of(from, to, true);
        fromFriend = friendTkService.queryOne(fromFriend);
        if (fromFriend == null) {
            return true;
        }
        if (friendTkService.removeFriend(from, to)) {
            String fromKey = genKey(from, to);
            String toKey = genKey(to, from);
            SmartRedisManager.getInstance().del(fromKey, toKey);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, String> getFriendRemarks(Long id) {
        String key = genRemarkKey(id);
        Map<String, String> map = SmartRedisManager.getInstance().hGetAll(key);
        if (map.isEmpty()) {
            //search from db.
            List<ImFriend> imFriends = friendTkService.queryList(ImFriend.of(id, null));
            if (CollectionUtils.isEmpty(imFriends)) {
                map = MapUtil.newHashMap(2);
                map.put(id.toString(), StringUtils.EMPTY);
            } else {
                map = imFriends.stream().collect(Collectors.toMap(f -> f.getUserId().toString(), ImFriend::getRemark));
            }
            SmartRedisManager.getInstance().hmSet(key, map);
        }
        return map;
    }


    private String genKey(Long from, Long to) {
        return KEY + UNION + from + UNION + to;
    }

    private String genRemarkKey(Long id) {
        return REMARK_KEY + UNION + id;
    }





}
