package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.PrimaryLessTkMapper;
import com.hqy.cloud.db.tk.support.PrimaryLessTkServiceImpl;
import com.hqy.cloud.message.bind.dto.ContactsDTO;
import com.hqy.cloud.message.tk.entity.ImFriend;
import com.hqy.cloud.message.tk.entity.ImFriendApplication;
import com.hqy.cloud.message.tk.mapper.ImFriendApplicationMapper;
import com.hqy.cloud.message.tk.mapper.ImFriendMapper;
import com.hqy.cloud.message.tk.service.ImFriendTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:15
 */
@Service
@RequiredArgsConstructor
public class ImFriendTkServiceImpl extends PrimaryLessTkServiceImpl<ImFriend> implements ImFriendTkService {
    private final ImFriendMapper mapper;
    private final ImFriendApplicationMapper applicationMapper;

    @Override
    public PrimaryLessTkMapper<ImFriend> getTkDao() {
        return mapper;
    }

    @Override
    public boolean removeFriend(Long from, Long to) {
        return mapper.removeFriend(from, to) > 0;
    }

    @Override
    public List<ImFriend> queryFriends(Long id, List<Long> userIds) {
        return mapper.queryFriends(id, userIds);
    }

    @Override
    public ContactsDTO queryContactByUserId(Long userId) {
        List<ImFriend> imFriends = mapper.select(ImFriend.of(userId, null));
        int unread = applicationMapper.selectUnread(userId);
        return new ContactsDTO(unread, imFriends);
    }

    @Override
    public Boolean updateImFriend(ImFriend imFriend) {
        return mapper.updateImFriend(imFriend) > 0;
    }
}
