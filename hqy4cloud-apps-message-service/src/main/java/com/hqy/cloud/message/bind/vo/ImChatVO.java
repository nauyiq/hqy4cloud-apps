package com.hqy.cloud.message.bind.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/22 10:12
 */
@Data
@NoArgsConstructor
public class ImChatVO {

    /**
     * 会话列表
     */
    private List<ConversationVO> conversations;

    /**
     * 联系人列表
     */
    private ContactsVO contacts;

    /**
     * 好友列表
     */
    private List<IndexFriendsVO> friends;



    public ImChatVO(List<ConversationVO> conversations, ContactsVO contacts) {
        this.conversations = conversations;
        this.contacts = contacts;
        if (this.contacts != null && CollectionUtils.isNotEmpty(contacts.getContacts())) {
            List<ContactVO> vos = contacts.getContacts();
            Map<String, List<ContactVO>> map = vos.stream().filter(vo -> !vo.getIsGroup()).collect(Collectors.groupingBy(ContactVO::getIndex));
            friends = map.entrySet().stream().map(entry -> {
                List<ContactVO> value = entry.getValue();
                List<FriendVO> toList = value.stream().map(contact -> new FriendVO(contact.getId(), contact.getAvatar(), contact.getDisplayName(), null)).toList();
                return new IndexFriendsVO(entry.getKey(), toList);
            }).toList();
        } else {
            this.friends = new ArrayList<>();
        }

    }
}
