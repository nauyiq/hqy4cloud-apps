package com.hqy.cloud.message.service.impl;

import com.hqy.cloud.message.bind.dto.GroupDTO;
import com.hqy.cloud.message.service.ImGroupOperationsService;
import com.hqy.cloud.message.tk.service.ImGroupMemberTkService;
import com.hqy.cloud.message.tk.service.ImGroupTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:37
 */
@Service
@RequiredArgsConstructor
public class ImGroupOperationsServiceImpl implements ImGroupOperationsService {
    private final TransactionTemplate template;
    private final ImGroupTkService groupTkService;
    private final ImGroupMemberTkService groupMemberTkService;


    @Override
    public boolean createGroup(Long id, GroupDTO createGroup) {
        return false;
    }
}
