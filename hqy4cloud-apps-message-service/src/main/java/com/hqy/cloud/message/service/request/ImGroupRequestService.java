package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.GroupDTO;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/14 17:38
 */
public interface ImGroupRequestService {

    /**
     * 新建群聊
     * @param id          创建者id
     * @param createGroup {@link GroupDTO}
     * @return            R.
     */
    R<Boolean> createGroup(Long id, GroupDTO createGroup);

}
