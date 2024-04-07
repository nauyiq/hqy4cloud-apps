package com.hqy.cloud.message.server.support.convertor;

import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.server.AbstractMessageConvertor;

import java.util.List;

/**
 * 文本类型的消息转换
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/27
 */
public class TextMessageConvertor extends AbstractMessageConvertor {

    @Override
    public List<Integer> supportTypes() {
        return List.of(MessageType.TEXT.type);
    }

    @Override
    protected ImMessageVO doProcess(Long loginUser, ImMessageVO message) {
        // 纯文本类型消息， 直接返回消息体即可
        return message;
    }

    @Override
    public String processByConversation(Long loginUser, String content, boolean group) {
        // 纯文本类型消息， 直接返回消息体即可
        return content;
    }
}
