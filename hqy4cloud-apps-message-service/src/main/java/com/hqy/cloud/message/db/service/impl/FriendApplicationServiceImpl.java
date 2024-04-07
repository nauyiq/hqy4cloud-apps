package com.hqy.cloud.message.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hqy.cloud.db.mybatisplus.BasePlusServiceImpl;
import com.hqy.cloud.message.bind.dto.FriendApplicationDTO;
import com.hqy.cloud.message.bind.enums.ImFriendApplicationState;
import com.hqy.cloud.message.db.entity.FriendApplication;
import com.hqy.cloud.message.db.mapper.FriendApplicationMapper;
import com.hqy.cloud.message.db.service.IFriendApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/7
 */
@Slf4j
@Service
public class FriendApplicationServiceImpl extends BasePlusServiceImpl<FriendApplication, FriendApplicationMapper> implements IFriendApplicationService {


    @Override
    public FriendApplication getByApplyAndReceive(Long apply, Long receive) {
        List<FriendApplication> list = query().eq("apply", apply)
                .eq("receive", receive)
                .eq("deleted", 0).list();
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<FriendApplicationDTO> queryApplicationByUserId(Long userId) {
        return baseMapper.queryApplicationByUserId(userId);
    }

    @Override
    public boolean checkIsRequestApplicationAndStateIsValid(Long applyId, Long receiveId) {
        QueryWrapper<FriendApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("apply", applyId)
                .eq("receive", receiveId)
                .eq("deleted", 0);
        FriendApplication application = baseMapper.selectOne(queryWrapper);
        if (application == null) {
            return false;
        }
        Integer status = application.getStatus();
        return status != null && (
                status.equals(ImFriendApplicationState.UN_READ.state) ||
                        status.equals(ImFriendApplicationState.ALREADY_READ.state));
    }

    @Override
    public boolean insertOrUpdate(List<FriendApplication> applications) {
        return baseMapper.insertOrUpdate(applications) > 0;
    }

    @Override
    public void updateApplicationsStatus(List<Long> ids, Integer status) {
        baseMapper.updateApplicationsStatus(ids, status);
    }


    @Override
    public void updateApplicationStatusByApplyAndReceive(Long apply, Long receiver, Integer status) {
        baseMapper.updateApplicationStatusByApplyAndReceive(apply, receiver, status);
    }
}
