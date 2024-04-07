package com.hqy.cloud.message.server.support.convertor;

import com.hqy.cloud.message.bind.ImLanguageContext;
import com.hqy.cloud.message.bind.PropertiesConstants;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.server.AbstractMessageConvertor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 添加朋友成功后的提示消息
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/27
 */
public class AddFriendNoticeMessageConvertor extends AbstractMessageConvertor {

    @Override
    public List<Integer> supportTypes() {
        return List.of(EventMessageType.ADD_FRIEND_NOTICE.type);
    }

    @Override
    protected ImMessageVO doProcess(Long loginUser, ImMessageVO message) {
        String value = ImLanguageContext.getValue(PropertiesConstants.ADD_FRIEND_NOTICE_KEY);
        if (StringUtils.isNotBlank(value)) {
            message.setContent(value);
        }
        return message;
    }

    @Override
    public String processByConversation(Long loginUser, String content, boolean group) {
        // 会话不用显示内容
        return StringUtils.EMPTY;
    }
}
