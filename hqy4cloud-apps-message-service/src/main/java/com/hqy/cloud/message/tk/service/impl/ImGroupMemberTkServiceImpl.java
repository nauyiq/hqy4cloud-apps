package com.hqy.cloud.message.tk.service.impl;

import com.hqy.cloud.db.tk.PrimaryLessTkMapper;
import com.hqy.cloud.db.tk.support.PrimaryLessTkServiceImpl;
import com.hqy.cloud.message.tk.entity.ImGroupMember;
import com.hqy.cloud.message.tk.mapper.ImGroupMemberMapper;
import com.hqy.cloud.message.tk.service.ImGroupMemberTkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:31
 */
@Service
@RequiredArgsConstructor
public class ImGroupMemberTkServiceImpl extends PrimaryLessTkServiceImpl<ImGroupMember> implements ImGroupMemberTkService {
    private final ImGroupMemberMapper mapper;

    @Override
    public PrimaryLessTkMapper<ImGroupMember> getTkDao() {
        return mapper;
    }
}
