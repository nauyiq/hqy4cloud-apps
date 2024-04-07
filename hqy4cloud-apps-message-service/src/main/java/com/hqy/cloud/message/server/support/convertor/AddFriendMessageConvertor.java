package com.hqy.cloud.message.server.support.convertor;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.message.bind.ConvertUtil;
import com.hqy.cloud.message.bind.ImLanguageContext;
import com.hqy.cloud.message.bind.PropertiesConstants;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.server.AbstractMessageConvertor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 添加好友消息转换器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/27
 */
public class AddFriendMessageConvertor extends AbstractMessageConvertor {

    @Override
    public List<Integer> supportTypes() {
        return List.of(EventMessageType.FRIEND.type);
    }

    @Override
    protected ImMessageVO doProcess(Long loginUser, ImMessageVO message) {
        String content = message.getContent();
        // 消息发送人
        UserInfoVO fromUser = message.getFromUser();
        Long sender = Long.valueOf(fromUser.getId());
        if (sender.equals(loginUser)) {
            // 发送人和登录用户是同一个人
            content = ImLanguageContext.getValue(PropertiesConstants.ADD_FRIEND_MESSAGE_KEY);
            // 翻转一下发送人
            message.setFromUser(message.getContactUser());
        } else if (StringUtils.isBlank(content)) {
            // 消息内容为空时
            String value = ImLanguageContext.getValue(PropertiesConstants.ADD_FRIEND_DEFAULT_APPLY_KEY);
            content = ConvertUtil.getReplaceValue(value, fromUser.getDisplayName());
        }
        message.setContent(content);
        return message;
    }

    @Override
    public String processByConversation(Long loginUser, String content, boolean group) {
        // 会话不存储此类型的内容
        return StrUtil.EMPTY;
    }
}
