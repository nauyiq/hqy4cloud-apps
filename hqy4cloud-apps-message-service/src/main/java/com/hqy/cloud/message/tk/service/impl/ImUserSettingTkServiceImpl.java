package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.message.tk.entity.ImUserSetting;
import com.hqy.cloud.message.tk.mapper.ImUserSettingMapper;
import com.hqy.cloud.message.tk.service.ImUserSettingTkService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:07
 */
@Service
@RequiredArgsConstructor
public class ImUserSettingTkServiceImpl extends BaseTkServiceImpl<ImUserSetting, Long> implements ImUserSettingTkService {
    private final ImUserSettingMapper mapper;

    @Override
    public BaseTkMapper<ImUserSetting, Long> getTkMapper() {
        return mapper;
    }

    @Override
    public boolean enabledPrivateChat(Long id) {
        Example example = new Example(ImUserSetting.class);
        example.createCriteria().andEqualTo("id", id);
        example.excludeProperties("inviteGroup", "oline", "clearMsg", "clearMsgDate");
        List<ImUserSetting> imUserSettings = mapper.selectByExample(example);
        if (CollectionUtils.isEmpty(imUserSettings)) {
            return false;
        }
        return imUserSettings.get(0).getPrivateChat();
    }

    @Override
    public boolean allEnablePrivateChat(List<Long> ids) {
        Example example = new Example(ImUserSetting.class);
        example.createCriteria().andIn("id", ids);
        example.excludeProperties("inviteGroup", "oline", "clearMsg", "clearMsgDate");
        List<ImUserSetting> imUserSettings = mapper.selectByExample(example);
        if (CollectionUtils.isEmpty(imUserSettings) || imUserSettings.size() != ids.size()) {
            return false;
        }
        return imUserSettings.parallelStream().allMatch(ImUserSetting::getPrivateChat);
    }
}
