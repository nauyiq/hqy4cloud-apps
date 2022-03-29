package com.hqy.message.service;

import com.hqy.ex.NettyContextHelper;
import com.hqy.rpc.api.AbstractRPCService;
import com.hqy.util.thread.ExecutorServiceProject;
import com.hqy.util.thread.ParentExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/24 17:20
 */
@Service
public class SocketIoMessagePushServiceImpl extends AbstractRPCService implements SocketIoMessagePushService {

    private static final Logger log = LoggerFactory.getLogger(SocketIoMessagePushServiceImpl.class);

    @Override
    public boolean syncPush(String bizId, String eventName, String wsMessageJson) {
        try {
            return NettyContextHelper.doPush(bizId, eventName, wsMessageJson);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean syncPushMultiple(Set<String> bizIdSet, String eventName, String wsMessageJson) {
        try {
            for (String bizId : bizIdSet) {
                NettyContextHelper.doPush(bizId, eventName, wsMessageJson);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean asyncPush(String bizId, String eventName, String wsMessageJson) {
        ParentExecutorService.getInstance().execute(() ->
                NettyContextHelper.doPush(bizId, eventName, wsMessageJson), ExecutorServiceProject.PRIORITY_IMMEDIATE);
        return true;
    }

    @Override
    public boolean asyncPushMultiple(Set<String> bizIdSet, String eventName, String wsMessageJson) {
        ParentExecutorService.getInstance().execute(() -> {
            for (String bizId : bizIdSet) {
                NettyContextHelper.doPush(bizId, eventName, wsMessageJson);
            }
        }, ExecutorServiceProject.PRIORITY_IMMEDIATE);
        return true;
    }




}
