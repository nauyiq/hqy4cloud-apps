package com.hqy.cloud.message.server.support.convertor;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.message.bind.ConvertUtil;
import com.hqy.cloud.message.bind.ImLanguageContext;
import com.hqy.cloud.message.bind.PropertiesConstants;
import com.hqy.cloud.message.bind.dto.ImMessageEventContentDTO;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.server.AbstractMessageConvertor;
import com.hqy.cloud.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 事件类型的消息转换器, 根据事件类型配置的key进行转换
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/27
 */
@Slf4j
public class EventsMessageConvertor extends AbstractMessageConvertor {

    @Override
    public List<Integer> supportTypes() {
        return List.of(EventMessageType.EVENT_CREATE_GROUP.type,
                EventMessageType.EVENT_GROUP_NAME_MODIFIED.type, EventMessageType.EVENT_GROUP_NOTICE_MODIFIED.type,
                EventMessageType.EVENT_GROUP_MEMBER_REMOVED.type, EventMessageType.EVENT_GROUP_REMOVED.type);
    }

    @Override
    protected ImMessageVO doProcess(Long loginUser, ImMessageVO message) {
        // 消息内容
        String content = message.getContent();
        if (StringUtils.isBlank(content)) {
            return message;
        }
        // 获取消息类型
        EventMessageType type = EventMessageType.getType(message.getMessageType());
        if (type == null) {
            log.warn("Message type is null, message: {}.", message.getMessageId());
            return message;
        }

        ImMessageEventContentDTO data = JsonUtil.toBean(content, ImMessageEventContentDTO.class);
        String operator;
        if (data.getOperatorId().equals(loginUser)) {
            // 操作人是自己
            operator = ImLanguageContext.getValue(PropertiesConstants.YOUR_KEY);
        } else {
            operator = data.getOperatorName();
        }
        String value = ImLanguageContext.getValue(type.translateKey);
        content = ConvertUtil.getReplaceValue(value, operator);
        message.setContent(content);
        return message;
    }


    @Override
    public String processByConversation(Long loginUser, String content, boolean group) {
        return StrUtil.EMPTY;
    }
}
