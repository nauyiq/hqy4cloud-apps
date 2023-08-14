package com.hqy.cloud.message.service.request.impl;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.service.ImGroupOperationsService;
import com.hqy.cloud.message.service.request.ImGroupRequestService;
import com.hqy.cloud.message.tk.entity.ImGroup;
import com.hqy.cloud.message.tk.service.ImGroupTkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.hqy.cloud.apps.commom.result.AppsResultCode.IM_GROUP_EXIST;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:38
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImGroupRequestServiceImpl implements ImGroupRequestService {
    private final ImGroupTkService groupTkService;
    private final ImGroupOperationsService groupOperationsService;

    @Override
    public R<Boolean> createGroup(Long id, GroupDTO createGroup) {
        //判断当前群聊是否存在. 同一个用户创建的群聊名称不能一致.
        ImGroup group = ImGroup.of(createGroup.getName(), id);
        group = groupTkService.queryOne(group);
        if (group != null) {
            return R.failed(IM_GROUP_EXIST);
        }
        return groupOperationsService.createGroup(id, createGroup) ? R.ok() : R.failed();
    }
}
