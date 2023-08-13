package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.vo.UserImSettingVO;

/**
 * @author qiyuan.hong
 * @date 2023-08-12 12:00
 */
public interface ImUserRequestService {

    /**
     * 获取用户聊天设置
     * @param  id 用户id
     * @return {@link UserImSettingVO}
     */
    R<UserImSettingVO> getUserImSetting(Long id);
}
