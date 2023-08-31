package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.ImChatConfigDTO;
import com.hqy.cloud.message.bind.vo.ConversationVO;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 17:13
 */
public interface ImConversationRequestService {

    /**
     * 获取聊天会话
     * @param id 用户id
     * @return   R.
     */
    R<List<ConversationVO>> getConversations(Long id);

    /**
     * update chat top.
     * @param id          user id.
     * @param chatConfig {@link ImChatConfigDTO}
     * @return           R.
     */
    R<Boolean> updateChatTop(Long id, ImChatConfigDTO chatConfig);

    /**
     * update chat notice.
     * @param id          user id.
     * @param chatConfig {@link ImChatConfigDTO}
     * @return           R.
     */
    R<Boolean> updateChatNotice(Long id, ImChatConfigDTO chatConfig);
}
