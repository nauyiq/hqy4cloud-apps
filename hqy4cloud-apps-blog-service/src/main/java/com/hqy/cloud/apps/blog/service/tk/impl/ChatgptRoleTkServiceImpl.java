package com.hqy.cloud.apps.blog.service.tk.impl;

import com.hqy.cloud.apps.blog.entity.ChatgptRole;
import com.hqy.cloud.apps.blog.mapper.ChatgptRoleMapper;
import com.hqy.cloud.apps.blog.service.tk.ChatgptRoleTkService;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:32
 */
@Service
@RequiredArgsConstructor
public class ChatgptRoleTkServiceImpl extends BaseTkServiceImpl<ChatgptRole, Long> implements ChatgptRoleTkService {
    private final ChatgptRoleMapper mapper;

    @Override
    public BaseTkMapper<ChatgptRole, Long> getTkMapper() {
        return this.mapper;
    }
}
