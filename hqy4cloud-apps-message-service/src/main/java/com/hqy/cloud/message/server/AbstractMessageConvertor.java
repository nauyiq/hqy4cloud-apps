package com.hqy.cloud.message.server;

import com.hqy.cloud.message.bind.vo.ImMessageVO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/27
 */
@Slf4j
public abstract class AbstractMessageConvertor implements MessageConvertor {

    @Override
    public ImMessageVO process(Long loginUser, ImMessageVO message) {
        if (message == null) {
            log.warn("MessageVo is empty, check input params, loginUser: {}.", loginUser);
            return null;
        }
        try {
            return doProcess(loginUser, message);
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return message;
        }
    }

    /**
     * 交给不同类型的子类去做类型处理.
     * @param loginUser 登录用户
     * @param message   待处理的消息
     * @return          处理好的消息
     */
    protected abstract ImMessageVO doProcess(Long loginUser, ImMessageVO message);
}
