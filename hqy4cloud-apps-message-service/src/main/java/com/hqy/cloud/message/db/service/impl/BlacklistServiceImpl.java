package com.hqy.cloud.message.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hqy.cloud.db.mybatisplus.BasePlusServiceImpl;
import com.hqy.cloud.message.bind.enums.BlacklistState;
import com.hqy.cloud.message.db.entity.Blacklist;
import com.hqy.cloud.message.db.mapper.BlacklistMapper;
import com.hqy.cloud.message.db.service.IBlacklistService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 黑名单表 服务实现类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-04
 */
@Service
public class BlacklistServiceImpl extends BasePlusServiceImpl<Blacklist, BlacklistMapper> implements IBlacklistService {

    @Override
    public BlacklistState selectBlacklistState(Long firstId, Long secondId) {
        List<Long> userIds = baseMapper.selectUserIds(firstId, secondId);
        if (CollectionUtils.isEmpty(userIds)) {
            return BlacklistState.NONE;
        }
        if (userIds.size() == 1) {
            // 只查询到一条，说明是单方面拉黑
            Long userId = userIds.get(0);
            // 拉黑的用户就是第一个用户 那就是第一个用户拉黑了第二个用户
            return userId.equals(firstId) ? BlacklistState.BLACKED_TO : BlacklistState.BLACKED_FROM;
        } else {
            // 互相拉黑的情况下，优先显示你拉黑了对方
            return BlacklistState.BLACKED_TO;
        }
    }

    @Override
    public boolean removeByUserIdAndBlackId(Long userId, Long blackId) {
        QueryWrapper<Blacklist> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("black_id", blackId);
        return remove(wrapper);
    }
}
