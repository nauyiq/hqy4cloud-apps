package com.hqy.cloud.message.server;

import com.hqy.cloud.message.bind.vo.ImMessageVO;

import java.util.List;

/**
 * 消息转换类， 为啥需要消息转换呢？ 因为聊天记录是集中存储在服务端，因此一条消息展示给不同视角的用户时其显示的内容可能不一致
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/26
 */
public interface MessageConvertor {

    /**
     * 支持的消息类型, 多种消息类型可以共用同一个消息转换器， 但是一个消息类型有且只能对应一个消息转换器
     * @return 消息类型, 枚举 {@link com.hqy.cloud.message.bind.enums.MessageType} {@link com.hqy.cloud.message.bind.enums.EventMessageType}
     */
    List<Integer> supportTypes();

    /**
     * 处理需要展示的消息内容
     * @param loginUser 登录的用户
     * @param message   查询出来的消息
     * @return          处理好的消息
     */
    ImMessageVO process(Long loginUser, ImMessageVO message);


    /**
     * 处理需要会话的最后一条消息内容
     * @param loginUser 登录的用户
     * @param content   消息内容
     * @param group     是否是群聊
     * @return          处理好的消息内容
     */
    String processByConversation(Long loginUser, String content, boolean group);

}
