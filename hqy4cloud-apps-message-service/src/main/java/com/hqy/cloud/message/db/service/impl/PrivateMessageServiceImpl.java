package com.hqy.cloud.message.db.service.impl;

import com.hqy.cloud.db.mybatisplus.BasePlusServiceImpl;
import com.hqy.cloud.message.bind.dto.ChatMessageDTO;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.message.db.entity.PrivateMessage;
import com.hqy.cloud.message.db.mapper.PrivateMessageMapper;
import com.hqy.cloud.message.db.service.IPrivateMessageService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 私聊聊天消息表 服务实现类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
@Service
public class PrivateMessageServiceImpl extends BasePlusServiceImpl<PrivateMessage, PrivateMessageMapper> implements IPrivateMessageService {

    @Override
    public List<ChatMessageDTO> selectMessages(Long userId, Long contactId, Long lastRemoveTime) {
        Date lastRemoveDate = lastRemoveTime == null ? null : new Date(lastRemoveTime);
        return baseMapper.selectMessages(userId, contactId, lastRemoveDate);
    }

    @Override
    public List<Long> selectUnreadMessageIds(Long userId, Long contactId) {
        return baseMapper.selectUnreadMessageIds(userId, contactId);
    }

    @Override
    public boolean readMessages(List<Long> unreadMessageIds) {
        return baseMapper.readMessages(unreadMessageIds) > 0;
    }

    @Override
    public boolean undoMessage(Long messageId, String content) {
        PrivateMessage message = new PrivateMessage();
        message.setId(messageId);
        message.setType(EventMessageType.UNDO.type);
        message.setContent(content);
        return this.updateById(message);
    }
}
