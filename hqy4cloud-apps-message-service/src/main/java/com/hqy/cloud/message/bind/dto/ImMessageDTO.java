package com.hqy.cloud.message.bind.dto;

import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
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
}
