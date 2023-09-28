package com.hqy.cloud.message.bind.dto;

import com.hqy.cloud.apps.commom.constants.AppsConstants;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.tk.entity.ImMessage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 16:34
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ImMessageDTO extends ImMessageVO {

    private Long conversationId;

    public boolean checkParams() {
        return StringUtils.isNotBlank(getToContactId()) && !StringUtils.isBlank(getContent()) && ImMessageType.isEnabled(getType());
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }


    public ImMessageDTO(ImMessage message) {
        setId(message.getId().toString());
        setMessageId(message.getMessageId());
        setIsGroup(message.getGroup());
        setIsRead(message.getRead());
        setFromUser(new UserInfoVO(message.getFrom().toString()));
        setToContactId(message.getTo().toString());
        setContent(message.getContent());
        setStatus(message.getStatus() ? AppsConstants.Message.IM_MESSAGE_SUCCESS : AppsConstants.Message.IM_MESSAGE_FAILED);
        setType(message.getType());
        setSendTime(message.getCreated().getTime());
    }

}
