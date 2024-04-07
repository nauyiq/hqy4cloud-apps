package com.hqy.cloud.message.db.service.impl;

import com.hqy.cloud.db.mybatisplus.BasePlusServiceImpl;
import com.hqy.cloud.message.bind.dto.ChatMessageDTO;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.message.db.entity.GroupConversation;
import com.hqy.cloud.message.db.entity.GroupMessage;
import com.hqy.cloud.message.db.mapper.GroupMessageMapper;
import com.hqy.cloud.message.db.service.IGroupMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 群聊消息表 服务实现类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
@Service
public class GroupMessageServiceImpl extends BasePlusServiceImpl<GroupMessage, GroupMessageMapper> implements IGroupMessageService {

    /**
     * 最大的未读消息数目
     */
    @Value("${im.group.unread.max: 99}")
    private Integer maxUnread;

    @Override
    public List<ChatMessageDTO> selectMessagesByGroupId(Long groupId, Long lastRemoveTime) {
        Date lastRemoveDate = lastRemoveTime == null ? null : new Date(lastRemoveTime);
        return baseMapper.selectMessagesByGroupId(groupId, lastRemoveDate);
    }

    @Override
    public List<ChatMessageDTO> selectRemovedGroupMemberMessages(GroupConversation groupConversation) {
        Long lastRemoveTime = groupConversation.getLastRemoveTime();
        Date lastRemoveDate = lastRemoveTime == null ? null : new Date(lastRemoveTime);
        return baseMapper.selectRemovedGroupMemberMessages(groupConversation.getGroupId(), lastRemoveDate, groupConversation.getUpdated());
    }

    @Override
    public List<Long> selectUnreadMessageIds(Long userId, Long lastReadTime, Long groupId) {
        Date lastReadDate = lastReadTime == null ? null : new Date(lastReadTime);
        return baseMapper.selectUnreadMessageIds(userId, lastReadDate, groupId, maxUnread);
    }


    @Override
    public boolean undoMessage(Long messageId, String content) {
        return baseMapper.updateMessageContentAndType(messageId, EventMessageType.UNDO.type, content) > 0;
    }
}
