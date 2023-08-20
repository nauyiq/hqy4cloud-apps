package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.vo.ConversationVO;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 17:13
 */
public interface ImContactRequestService {

    /**
     * 获取聊天会话
     * @param id 用户id
     * @return   R.
     */
    R<List<ConversationVO>> getConversations(Long id);
}
