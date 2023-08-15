package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.vo.ContactVO;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 17:13
 */
public interface ImContactRequestService {

    /**
     * 获取聊天联系人
     * @param id 用户id
     * @return   R.
     */
    R<ContactVO> getContacts(Long id);
}
