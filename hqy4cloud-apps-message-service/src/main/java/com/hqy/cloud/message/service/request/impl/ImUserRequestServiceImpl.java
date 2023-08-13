package com.hqy.cloud.message.service.request.impl;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.vo.UserImSettingVO;
import com.hqy.cloud.message.service.request.ImUserRequestService;
import com.hqy.cloud.message.tk.service.ImFriendTkService;
import com.hqy.cloud.message.tk.service.ImUserSettingTkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @date 2023-08-12 12:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImUserRequestServiceImpl implements ImUserRequestService {
    private final ImFriendTkService friendTkService;
    private final ImUserSettingTkService userSettingTkService;


    @Override
    public R<UserImSettingVO> getUserImSetting(Long id) {
        return null;
    }
}
