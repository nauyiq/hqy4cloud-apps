package com.hqy.cloud.message.service.impl;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.message.bind.dto.ImUserInfoDTO;
import com.hqy.cloud.message.service.UserSettingThriftService;
import com.hqy.cloud.message.service.transactional.TccAddImUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSettingThriftServiceImpl implements UserSettingThriftService {
    private final TccAddImUserService tccAddImUserService;

    @Override
    public boolean addImUser(Long id, String username, String nickname, String avatar) {
        ImUserInfoDTO userInfo = new ImUserInfoDTO(id, username, nickname, avatar, StrUtil.EMPTY);
        return tccAddImUserService.addImUser(userInfo);
    }
}
