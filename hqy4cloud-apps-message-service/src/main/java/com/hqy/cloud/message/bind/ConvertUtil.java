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

import static com.hqy.cloud.apps.commom.constants.AppsConstants.Message.*;

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

    public String getMessageContent(Long id, String editor, ImMessageDoc doc) {
        String type = doc.getType();
        Long from = doc.getFrom();
        if (ImMessageType.EVENT.type.equals(type)) {
            return getEventMessageContent(id, editor, from, doc.getGroup(), doc.getContent());
        } else if (ImMessageType.FILE.type.equals(type) || ImMessageType.IMAGE.type.equals(type)) {
            return doc.getPath();
        }
        return doc.getContent();
    }

    public String getEventMessageContent(Long id, String editor, Long from, boolean isGroup, String content) {
        if (id.equals(from)) {
            content = content.replace(REPLACE, YOU);
        } else if (AppsConstants.Message.UNDO_FROM_MESSAGE_CONTENT.equals(content) && !isGroup){
            content = content.replace(REPLACE, TARGET);
        } else {
            content = content.replace(REPLACE, editor);
        }
        return content;
    }


}
