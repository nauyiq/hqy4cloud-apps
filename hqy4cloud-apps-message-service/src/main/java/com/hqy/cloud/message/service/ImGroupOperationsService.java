package com.hqy.cloud.message.service;

import com.hqy.cloud.message.bind.dto.GroupDTO;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:37
 */
public interface ImGroupOperationsService {

    /**
     * 新建群聊
     * @param id          创建者id
     * @param createGroup {@link GroupDTO}
     * @return            result.
     */
    boolean createGroup(Long id, GroupDTO createGroup);
}
