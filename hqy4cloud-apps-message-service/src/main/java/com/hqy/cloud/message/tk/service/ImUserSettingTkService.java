package com.hqy.cloud.message.tk.service;

import com.hqy.cloud.db.tk.BaseTkService;
import com.hqy.cloud.message.tk.entity.ImUserSetting;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:05
 */
public interface ImUserSettingTkService extends BaseTkService<ImUserSetting, Long> {

    /**
     * 判断当前用户是否允许私聊
     * @param id 用户id
     * @return   result
     */
    boolean enabledPrivateChat(Long id);

    /**
     * 判断用户列表是否都允许私聊
     * @param ids 用户id集合
     * @return    result
     */
    boolean allEnablePrivateChat(List<Long> ids);
}
