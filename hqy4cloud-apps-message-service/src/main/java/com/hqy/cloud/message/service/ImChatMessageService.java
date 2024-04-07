package com.hqy.cloud.message.service;

import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.message.bind.dto.ForwardMessageDTO;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.db.entity.GroupConversation;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 聊天消息 service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
public interface ImChatMessageService {

    /**
     * 判断是否可以进行聊天
     * @param userId    用户id
     * @param contactId 联系人id
     * @param isGroup   是否是群聊
     * @return          业务响应码
     */
    AppsResultCode getEnableChatState(Long userId, Long contactId, boolean isGroup);

    /**
     * 获取聊天消息的分布式id
     * @param isGroup 是否是群聊
     * @return        聊天消息记录的分布式id
     */
    Long getDistributeMessageId(boolean isGroup);


    /**
     * 分页查找聊天会话
     * @param page           第几页
     * @param limit          一页几条
     * @param userId         用户id
     * @param contactId      联系人id
     * @param lastRemoveTime 上次移除会话时间
     * @param isGroup        是否是群聊
     * @return               分页聊天记录
     */
    PageResult<ImMessageVO> getPageMessages(Integer page, Integer limit, Long userId, Long contactId, Long lastRemoveTime, boolean isGroup);

    /**
     * 查找被移除群聊用户可以看到的聊天消息
     * @param page              第几页
     * @param limit             一个几行
     * @param groupConversation 群聊会话
     * @return                  聊天记录
     */
    PageResult<ImMessageVO> getRemovedGroupMemberMessages(Integer page, Integer limit, GroupConversation groupConversation);

    /**
     * 分页查找聊天记录，根据关键词类型等查询
     * @param page           第几页
     * @param limit          一页几条
     * @param accountId      账号id
     * @param contactId      联系人id
     * @param lastRemoveTime 上次移除会话时间
     * @param messageType    消息类型
     * @param keywords       关键词
     * @param group          是否群聊
     * @return               聊天记录
     */
    PageResult<ImMessageVO> searchPageMessages(Integer page, Integer limit, Long accountId, Long contactId, Long lastRemoveTime,
                                               MessageType messageType, String keywords, Boolean group);

    /**
     * 分页查找聊天记录，根据关键词类型等查询， 查询被移除群聊用户可搜索的聊天记录
     * @param page              第几页
     * @param limit             一页几条
     * @param groupConversation 群聊会话
     * @param messageType       消息类型
     * @param keywords          关键词
     * @return                  聊天记录
     */
    PageResult<ImMessageVO> searchRemovedGroupMemberMessages(Integer page, Integer limit, GroupConversation groupConversation, MessageType messageType, String keywords);

    /**
     * 发送私聊消息
     * @param id       发送用户id
     * @param friendId 好友id
     * @param message  消息体
     * @return         消息VO对象
     */
    ImMessageVO sendPrivateMessage(Long id, Long friendId, ImMessageDTO message);

    /**
     * 发送添加好友消息
     * @param userId   用户id
     * @param friendId 好友id
     * @param content  消息内容
     * @return         是否发送成功
     */
    boolean sendAddFriendMessage(Long userId, Long friendId, String content);

    /**
     * 发送群聊消息
     * @param id       发送用户id
     * @param groupId  群聊id
     * @param message  消息体
     * @return         消息VO对象
     */
    ImMessageVO sendGroupMessage(Long id, Long groupId, ImMessageDTO message);

    /**
     * 撤回消息
     * @param isGroup   是否是群聊
     * @param userId    撤回消息的用户id
     * @param contactId 联系人id
     * @param id        消息表主键
     * @param messageId 消息id
     * @param created   消息的创建时间
     * @return          消息撤回后的内容
     */
    String undoMessage(boolean isGroup, Long userId, Long contactId, Long id, String messageId, Date created);


    /**
     * 添加事件消息到db，
     * @param group         是否是群聊
     * @param sender        发送人信息
     * @param contactId     联系人id
     * @param receives      receives为空时则不发送事件消息给客户端
     * @param messageType   消息类型
     * @return              是否成功
     */
    boolean addEventMessage(boolean group, UserInfoVO sender, Long contactId, Collection<Long> receives, EventMessageType messageType);


    /**
     * 添加事件消息到db，
     * @param group         是否是群聊
     * @param sender        发送人信息
     * @param contactId     联系人id
     * @param receives      receives为空时则不发送事件消息给客户端
     * @param messageType   消息类型
     * @param data         指定添加事件时间
     * @return             消息VO
     */
    boolean addEventMessage(boolean group, UserInfoVO sender, Long contactId, Collection<Long> receives, EventMessageType messageType, Date data);

    /**
     * 设置私聊消息为已读
     * @param userId    用户id
     * @param contactId 联系人id
     * @return          已读消息id列表
     */
    List<String> readPrivateMessages(Long userId, Long contactId);

    /**
     * 设置群聊消息为已读
     * @param groupConversation 群聊会话
     * @return                  已读消息id列表
     */
    List<String> readGroupMessages(GroupConversation groupConversation);

    /**
     * 转发消息
     * @param accountId      登录用户id
     * @param content        内容
     * @param type           类型
     * @param forwardMessage 转发人
     * @return               转发的消息，包括是否转发成功等
     */
    List<ImMessageVO> forwardMessage(Long accountId, String content, Integer type, ForwardMessageDTO forwardMessage);

    /**
     * 删除群聊消息
     * @param groupId 群聊id
     * @return        是否删除成功
     */
    boolean deleteGroupMessages(Long groupId);



}
