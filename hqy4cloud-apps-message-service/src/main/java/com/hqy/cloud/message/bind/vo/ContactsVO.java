package com.hqy.cloud.message.bind.vo;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/30 17:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactsVO {

    /**
     * 好友申请列表未读消息数
     */
    private Integer applicationUnread;

    /**
     * 联系人列表
     */
    private List<ContactVO> contacts;

    /**
     * 根据下标分好组的好友列表
     */
    private List<IndexFriendsVO> friends;


    public static ContactsVO of() {
        return of(0, Collections.emptyList());
    }

    public static ContactsVO of(Integer unread, List<ContactVO> contacts) {
        // 根据好友下标联系人分组
        Map<String, List<ContactVO>> groupIndexMap = contacts.stream().filter(contact -> !contact.getIsGroup()).collect(Collectors.groupingBy(ContactVO::getIndex));
        List<IndexFriendsVO> indexFriends = groupIndexMap.entrySet().stream().map(entry -> {
            String index = entry.getKey();
            List<ContactVO> groupContacts = entry.getValue();
            List<FriendVO> friends = groupContacts.stream().map(contact ->
                    new FriendVO(contact.getId(), contact.getAvatar(), contact.getDisplayName(), StrUtil.EMPTY)).toList();
            return new IndexFriendsVO(index, friends);
        }).toList();
        return new ContactsVO(unread, contacts, indexFriends);
    }

}
