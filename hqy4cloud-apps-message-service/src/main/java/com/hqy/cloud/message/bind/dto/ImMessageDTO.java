package com.hqy.cloud.message.bind.dto;

import cn.hutool.core.lang.UUID;
import com.hqy.cloud.message.bind.enums.ImMessageState;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.db.entity.PrivateMessage;
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

    public boolean checkParamsIsUndefined() {
        return StringUtils.isAnyBlank(getToContactId(), getContent(), getType()) ||
                getIsGroup() == null || getFromUser() == null;

    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }


    public ImMessageDTO(PrivateMessage message, UserInfoVO infoVO) {
        setId(message.getMessageId());
        setMessageId(message.getId().toString());
        setIsGroup(false);
        setIsRead(message.getIsRead());
        setFromUser(infoVO);
        setToContactId(message.getReceive().toString());
        setContent(message.getContent());
        setStatus(ImMessageState.getState(message.getStatus()));
        setType(MessageType.getMessageType(message.getType()));
        setMessageType(message.getType());
        setSendTime(message.getCreated().getTime());
    }

    public ImMessageDTO(Long messageId, Boolean group, UserInfoVO fromUser, String content, Long contactId, String type) {
        setId(UUID.fastUUID().toString());
        setMessageId(messageId.toString());
        setIsGroup(group);
        setFromUser(fromUser);
        setIsRead(true);
        setContent(content);
        setType(type);
        setToContactId(contactId.toString());
        setStatus(ImMessageState.SUCCESS.name);
    }


}
