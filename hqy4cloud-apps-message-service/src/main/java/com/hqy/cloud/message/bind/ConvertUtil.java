package com.hqy.cloud.message.bind;

import com.hqy.cloud.apps.commom.constants.AppsConstants;
import com.hqy.cloud.message.bind.event.support.AddGroupEvent;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.es.document.ImMessageDoc;
import com.hqy.cloud.message.tk.entity.ImGroup;
import com.hqy.cloud.message.tk.entity.ImGroupMember;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/16 14:54
 */
@UtilityClass
public class ConvertUtil {

    public List<AddGroupEvent> newAddGroupEvent(List<ImGroupMember> groupMembers, ImGroup group) {
        return groupMembers.stream().map(groupMember -> {
            Long userId = groupMember.getUserId();
            return AddGroupEvent.builder()
                    .id(userId.toString())
                    .avatar(group.getAvatar())
                    .isGroup(true)
                    .isNotice(groupMember.getNotice())
                    .isTop(groupMember.getTop())
                    .unread(0)
                    .role(groupMember.getRole())
                    .invite(group.getInvite())
                    .notice(group.getNotice())
                    .creator(group.getCreator().toString()).build();
        }).collect(Collectors.toList());
    }

    public String getMessageContent(Long id, ImMessageDoc doc) {
        String type = doc.getType();
        Long from = doc.getFrom();
        if (ImMessageType.EVENT.type.equals(type) && !id.equals(from)) {
            return AppsConstants.Message.UNDO_TO_MESSAGE_CONTENT;
        } else if (ImMessageType.FILE.type.equals(type) || ImMessageType.IMAGE.type.equals(type)) {
            return doc.getPath();
        }
        return doc.getContent();
    }


}
