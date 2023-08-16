package com.hqy.cloud.message.bind;

import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.socketio.event.AddGroupEvent;
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


}
