package com.hqy.cloud.message.bind;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.hqy.cloud.apps.commom.constants.AppsConstants;
import com.hqy.cloud.message.bind.event.support.AddGroupEvent;
import com.hqy.cloud.message.common.im.enums.ImMessageType;
import com.hqy.cloud.message.es.document.ImMessageDoc;
import com.hqy.cloud.message.tk.entity.ImGroup;
import com.hqy.cloud.message.tk.entity.ImGroupMember;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/16 14:54
 */
@UtilityClass
public class ConvertUtil {
    public static final String DEFAULT_INDEX = "#";

    public String getIndex(String displayName) {
        if (StringUtils.isBlank(displayName)) {
            return DEFAULT_INDEX;
        }
        char fistChar = displayName.charAt(0);
        String fistChatStr = Character.toString(fistChar);
        return Validator.isLetter(fistChatStr) ? (PinyinUtil.getFirstLetter(fistChar) + "").toUpperCase() : "#";
    }

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
