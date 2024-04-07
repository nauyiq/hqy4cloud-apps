package com.hqy.cloud.message.server.support.convertor;

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
 * 撤回消息内容装换器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/27
 */
@Slf4j
public class UndoMessageConvertor extends AbstractMessageConvertor {

    @Override
    public List<Integer> supportTypes() {
        return List.of(EventMessageType.UNDO.type);
    }

    @Override
    protected ImMessageVO doProcess(Long loginUser, ImMessageVO message) {
        Boolean isGroup = message.getIsGroup();
        String content = message.getContent();
        message.setContent(getUndoMessageContent(loginUser, content, isGroup));
        return message;
    }

    @Override
    public String processByConversation(Long loginUser, String content, boolean group) {
        return getUndoMessageContent(loginUser, content, group);
    }

    private String getUndoMessageContent(Long loginUser, String content, Boolean group) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        // 获取撤回消息类型的撤回人和撤回人昵称或用户名
        try {
            ImMessageEventContentDTO data = JsonUtil.toBean(content, ImMessageEventContentDTO.class);
            if (loginUser.equals(data.getOperatorId())) {
                // 自己撤回了一条消息.
                content = ImLanguageContext.getValue(PropertiesConstants.UNDO_MESSAGE_BY_YOURSELF);
            } else {
                String value = ImLanguageContext.getValue(group ? PropertiesConstants.UNDO_MESSAGE_BY_GROUP : PropertiesConstants.UNDO_MESSAGE_BY_FRIEND);
                // 判断是否是群聊， 群聊的话因为不是自己撤回的需要显示撤回人名字，
                content = group ? ConvertUtil.getReplaceValue(value, data.getOperatorName()) : value;
            }
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
        }
        return content;
    }


}
