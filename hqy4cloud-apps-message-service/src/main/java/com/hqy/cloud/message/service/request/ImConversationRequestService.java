package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.message.bind.dto.ImChatConfigDTO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.bind.vo.ImChatVO;

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
     * 获取当前用户聊天列表（会话列表， 好友列表等）
     * @param userId 用户id
     * @return       R.
     */
    R<ImChatVO> getImChatInfo(Long userId);

    /**
     * 新增会话
     * @param id     登录用户id
     * @param userId 联系人id
     * @return       R.
     */
    R<ConversationVO> addConversation(Long id, Long userId);

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

    /**
     * 删除会话
     * @param userId         用户id
     * @param conversationId 会话id
     * @return               R.
     */
    R<Boolean> deleteConversation(Long userId, Long conversationId);


}
