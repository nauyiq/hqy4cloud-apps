package com.hqy.cloud.message.db.service;

import com.hqy.cloud.db.mybatisplus.BasePlusService;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.ImUserInfoDTO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.db.entity.PrivateConversation;
import com.hqy.cloud.message.db.entity.PrivateMessage;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 私聊聊天会话表 服务类
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-06
 */
public interface IPrivateConversationService extends BasePlusService<PrivateConversation> {

    /**
     * 查询聊天会话列表根据iD
     * @param userId 用户id
     * @return       聊会会话DTO
     */
    List<ConversationDTO> queryConversationsByUserId(Long userId);

    /**
     * 查询好友会话信息
     * @param userId   用户id
     * @param friendId 好友id
     * @return         会话信息
     */
    ConversationDTO queryConversationInfoByUserIdAndFriendId(Long userId, Long friendId);

    /**
     * 新增或更新会话列表， 并且返回对方的会话id
     * @param conversation  对方的会话
     * @param iConversation 发送方的会话
     * @return              对方的会话id
     */
    Long insertOrUpdateConversationAndReturnConversationId(PrivateConversation conversation, PrivateConversation iConversation);

    /**
     * 批量新增或更新
     * @param conversations 会话列表
     * @return              是否成功
     */
    boolean insertOrUpdate(List<PrivateConversation> conversations);

    /**
     * 发送新增私聊会话事件
     * @param conversation 会话实体
     * @param fromUser     发消息的人
     */
    void sendAppendPrivateChatEvent(PrivateConversation conversation, UserInfoVO fromUser);

    /**
     * 新增或插入添加好友的会话，最后返回当前两个用户的会话列表对象
     * @param userId        用户id
     * @param friendId      好友id
     * @param message       会话的最后一条消息
     * @param friendInfoMap 好友信息map
     * @return              会话列表
     */
    List<PrivateConversation> insertOrUpdateAddFriendConversations(Long userId, Long friendId, PrivateMessage message, Map<Long, ImUserInfoDTO> friendInfoMap);

    /**
     * 伪删除会话
     * @param userId   用户id
     * @param friendId 好友id
     * @return         是否删除成功
     */
    boolean removeConversation(Long userId, Long friendId);

    /**
     * 更新会话展示名
     * @param userId      用户id
     * @param contactId   联系人id
     * @param displayName 展示名
     * @return            是否更新成功
     */
    boolean updateConversationDisplayName(Long userId, Long contactId, String displayName);

    /**
     * 根据用户id和联系人id查找会话
     * @param id          用户id
     * @param toContactId 联系人id
     * @return            私聊会话
     */
    PrivateConversation queryByUserIdAndContactId(Long id, Long toContactId);

    /**
     * 更新会话，撤回消息
     * @param userId    用户id
     * @param contactId 联系人id
     * @param content   撤回消息内容
     * @return          是否撤回成功
     */
    boolean updateConversationUndoMessage(Long userId, Long contactId, String content);

    /**
     * 修改会话的置顶状态
     * @param conversationId 会话id
     * @param status         置顶状态
     * @return               是否修改成功
     */
    boolean updateConversationTopState(Long conversationId, Boolean status);

    /**
     * 修改会话的通知状态
     * @param conversationId 会话id
     * @param status         通知状态
     * @return               是否修改成功
     */
    boolean updateConversationNoticeState(Long conversationId, Boolean status);
}
