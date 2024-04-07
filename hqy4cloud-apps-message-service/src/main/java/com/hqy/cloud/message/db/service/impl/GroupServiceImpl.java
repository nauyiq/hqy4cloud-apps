package com.hqy.cloud.message.db.service.impl;

import com.hqy.cloud.db.mybatisplus.BasePlusServiceImpl;
import com.hqy.cloud.message.db.entity.Group;
import com.hqy.cloud.message.db.mapper.GroupMapper;
import com.hqy.cloud.message.db.service.IGroupService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * im群聊表 服务实现类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-05
 */
@Service
public class GroupServiceImpl extends BasePlusServiceImpl<Group, GroupMapper> implements IGroupService {

    @Override
    public boolean deletedGroup(Long groupId) {
        return baseMapper.deleteGroup(groupId) > 0;
    }
}
