package com.hqy.cloud.message.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hqy.cloud.db.mybatisplus.BasePlusServiceImpl;
import com.hqy.cloud.message.bind.dto.ContactDTO;
import com.hqy.cloud.message.bind.dto.ContactsDTO;
import com.hqy.cloud.message.db.entity.FriendState;
import com.hqy.cloud.message.db.mapper.FriendStateMapper;
import com.hqy.cloud.message.db.service.IFriendStateService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 好友状态表 服务实现类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
@Service
public class FriendStateServiceImpl extends BasePlusServiceImpl<FriendState, FriendStateMapper> implements IFriendStateService {

    @Override
    public FriendState getByUserIdAndFriendId(Long userId, Long friendId) {
        QueryWrapper<FriendState> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("friend_id", friendId)
                .eq("deleted", 0);
        return getOne(wrapper);
    }

    @Override
    public ContactsDTO queryContactsByUserId(Long userId) {
        // 查询好友联系人
        List<ContactDTO> contacts = baseMapper.queryContactsByUserId(userId);
        // 查询未读消息数
        Integer applicationUnread = baseMapper.queryApplicationUnread(userId);
        contacts = CollectionUtils.isEmpty(contacts) ? new ArrayList<>() : contacts;
        contacts.forEach(contactDTO ->  contactDTO.setIsGroup(false));
        applicationUnread = applicationUnread == null ? 0 : applicationUnread;
        return new ContactsDTO(applicationUnread, contacts);
    }

    @Override
    public List<FriendState> selectByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }


    @Override
    public boolean insertOrUpdate(List<FriendState> friendStates) {
        return baseMapper.insertOrUpdate(friendStates) > 0;
    }

    @Override
    public boolean removeState(Long userId, Long friendId) {
        return baseMapper.removeState(userId, friendId) > 0;
    }

    @Override
    public boolean updateTopState(Long userId, Long contactId, Boolean status) {
        return baseMapper.updateTopState(userId, contactId, status) > 0;
    }

    @Override
    public boolean updateNoticeState(Long userId, Long contactId, Boolean status) {
        return baseMapper.updateNoticeState(userId, contactId, status) > 0;
    }
}
