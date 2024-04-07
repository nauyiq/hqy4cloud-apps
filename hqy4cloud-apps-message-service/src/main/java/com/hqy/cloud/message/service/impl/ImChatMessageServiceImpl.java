package com.hqy.cloud.message.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.foundation.id.DistributedIdGen;
import com.hqy.cloud.message.bind.Constants;
import com.hqy.cloud.message.bind.ConvertUtil;
import com.hqy.cloud.message.bind.ImLanguageContext;
import com.hqy.cloud.message.bind.PropertiesConstants;
import com.hqy.cloud.message.bind.dto.*;
import com.hqy.cloud.message.bind.enums.BlacklistState;
import com.hqy.cloud.message.bind.enums.EventMessageType;
import com.hqy.cloud.message.bind.enums.ImMessageState;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.bind.event.support.GroupChatEvent;
import com.hqy.cloud.message.bind.event.support.MessageEventGroupChatEvent;
import com.hqy.cloud.message.bind.event.support.PrivateChatEvent;
import com.hqy.cloud.message.bind.event.support.UndoMessageEvent;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.db.entity.GroupConversation;
import com.hqy.cloud.message.db.entity.GroupMessage;
import com.hqy.cloud.message.db.entity.PrivateConversation;
import com.hqy.cloud.message.db.entity.PrivateMessage;
import com.hqy.cloud.message.db.service.*;
import com.hqy.cloud.message.es.document.ImMessage;
import com.hqy.cloud.message.es.service.ImMessageElasticService;
import com.hqy.cloud.message.server.ImEventListener;
import com.hqy.cloud.message.server.MessageFactory;
import com.hqy.cloud.message.service.ImChatMessageService;
import com.hqy.cloud.message.service.ImGroupService;
import com.hqy.cloud.message.service.ImUserRelationshipService;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.concurrent.IExecutorsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImChatMessageServiceImpl implements ImChatMessageService {
    private final ImEventListener imEventListener;
    private final TransactionTemplate transactionTemplate;
    private final IUserSettingService userSettingService;
    private final ImUserRelationshipService relationshipService;
    private final ImMessageElasticService imMessageElasticService;

    private final ImGroupService groupService;
    private final IGroupMessageService groupMessageService;
    private final IGroupConversationService groupConversationService;

    private final IPrivateMessageService privateMessageService;
    private final IPrivateConversationService privateConversationService;

    @Override
    public AppsResultCode getEnableChatState(Long userId, Long contactId, boolean isGroup) {
        if (isGroup) {
            // 判断是否是群聊成员.
            boolean isMember = groupService.isGroupMember(contactId, userId);
            return isMember ? AppsResultCode.SUCCESS : AppsResultCode.IM_NOT_GROUP_MEMBER;
        } else {
            // 判断是否是好友
            boolean friend = relationshipService.isFriend(userId, contactId);
            if (!friend) {
                return AppsResultCode.IM_NOT_FRIEND;
            }
            // 判断黑名单状态
            BlacklistState state = relationshipService.getBlacklistState(userId, contactId);
            if (state == BlacklistState.NONE) {
                return AppsResultCode.SUCCESS;
            }
            return state == BlacklistState.BLACKED_TO ? AppsResultCode.IM_BLACKLIST_TO : AppsResultCode.IM_BLACKLIST_FROM;
        }
    }

    @Override
    public Long getDistributeMessageId(boolean isGroup) {
        return DistributedIdGen.getSnowflakeId(isGroup ? Constants.IM_GROUP_MESSAGE_DISTRIBUTE_ID_SCENE : Constants.IM_PRIVATE_MESSAGE_DISTRIBUTE_ID_SCENE);
    }

    @Override
    public PageResult<ImMessageVO> getPageMessages(Integer page, Integer limit, Long userId, Long contactId, Long lastRemoveTime, boolean group) {
        // 分页查找聊天记录.
        PageHelper.startPage(page, limit);
        List<ChatMessageDTO> messages = group ? groupMessageService.selectMessagesByGroupId(contactId, lastRemoveTime) : privateMessageService.selectMessages(userId, contactId, lastRemoveTime);
        if (CollectionUtils.isEmpty(messages)) {
            return new PageResult<>();
        }
        // 聊天记录查出来后再根据消息创建时间升序排序
        messages.sort((m1, m2) -> DateUtil.compare(m1.getCreated(), m2.getCreated()));
        Set<Long> sendUserIds = messages.stream().map(ChatMessageDTO::getSend).collect(Collectors.toSet());
        Map<Long, UserInfoVO> userInfoMap = group ? groupService.getGroupMemberUserInfo(contactId, sendUserIds) : relationshipService.selectFriendMessageVO(userId, contactId);
        // 构建结果集
        List<ImMessageVO> resultMessages = messages.stream().map(message -> {
            UserInfoVO sender = userInfoMap.get(message.getSend());
            UserInfoVO receiver = userInfoMap.get(message.getToContactId());
            return MessageFactory.create(group, userId, message, sender, receiver);
        }).toList();
        PageInfo<ImMessageVO> pageInfo = new PageInfo<>(resultMessages);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), resultMessages);
    }

    @Override
    public PageResult<ImMessageVO> getRemovedGroupMemberMessages(Integer page, Integer limit, GroupConversation groupConversation) {
        // 分页查找聊天记录.
        PageHelper.startPage(page, limit);
        List<ChatMessageDTO> messages = groupMessageService.selectRemovedGroupMemberMessages(groupConversation);
        if (CollectionUtils.isEmpty(messages)) {
            return new PageResult<>();
        }
        messages.forEach(message -> message.setIsGroup(true));
        messages.sort((m1, m2) -> (int)(m1.getCreated().getTime() - m2.getCreated().getTime()));

        Set<Long> sendUserIds = messages.stream().map(ChatMessageDTO::getSend).collect(Collectors.toSet());
        Map<Long, UserInfoVO> userInfoMap = groupService.getGroupMemberUserInfo(groupConversation.getGroupId(), sendUserIds);
        List<ImMessageVO> resultMessages = messages.stream().map(message -> {
            UserInfoVO sender = userInfoMap.get(message.getSend());
            return MessageFactory.create(true, groupConversation.getUserId(), message, sender, null);
        }).toList();


        PageInfo<ImMessageVO> pageInfo = new PageInfo<>(resultMessages);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), resultMessages);
    }

    @Override
    public PageResult<ImMessageVO> searchPageMessages(Integer page, Integer limit, Long accountId, Long contactId, Long lastRemoveTime, MessageType messageType, String keywords, Boolean group) {
        PageResult<ImMessage> result = imMessageElasticService.searchPageMessages(page, limit, accountId, contactId, lastRemoveTime, messageType, keywords, group);
        return getSearchPageResultByEsPageResult(result, page, limit);
    }

    @Override
    public PageResult<ImMessageVO> searchRemovedGroupMemberMessages(Integer page, Integer limit, GroupConversation groupConversation, MessageType messageType, String keywords) {
        PageResult<ImMessage> result = imMessageElasticService.searchRemovedGroupMemberMessages(page, limit, groupConversation, messageType, keywords);
        return getSearchPageResultByEsPageResult(result, page, limit);
    }

    @NotNull
    private PageResult<ImMessageVO> getSearchPageResultByEsPageResult(PageResult<ImMessage> result, Integer page, Integer limit) {
        List<ImMessage> resultList = result.getResultList();
        if (CollectionUtils.isEmpty(resultList)) {
            return new PageResult<>();
        }
        // 所有发送人id集合
        Set<Long> ids = resultList.stream().map(ImMessage::getSend).collect(Collectors.toSet());
        Map<Long, UserInfoVO> userInfoMap = userSettingService.listByIds(ids).stream().map(userSetting -> new UserInfoVO(userSetting.getId().toString(), userSetting.getNickname(), userSetting.getAvatar()))
                .collect(Collectors.toMap(u -> Long.parseLong(u.getId()), Function.identity()));
        // 构建结果集
        List<ImMessageVO> resultMessages = resultList.stream().map(message -> {
            UserInfoVO sender = userInfoMap.get(message.getSend());
            return MessageFactory.create(message, sender);
        }).toList();
        return new PageResult<>(page, limit, result.getTotal(), resultMessages);
    }


    @Override
    public ImMessageVO sendPrivateMessage(Long userId, Long friendId, ImMessageDTO message) {
        // 获取消息类型
        Integer messageType = getMessageType(message);
        PrivateConversation conversation = PrivateConversation.of(friendId, userId, messageType, message);
        PrivateConversation iConversation = PrivateConversation.of(userId, friendId, messageType, message);
        // 获取分布式私聊聊天记录id
        Long id = this.getDistributeMessageId(false);
        // 构建私聊聊天实体
        PrivateMessage privateMessage = PrivateMessage.of(id, userId, friendId, messageType, message);

        Boolean result = transactionTemplate.execute(status -> {
            try {
                // 新增或更新会话，并且返回对方和当前发送人的会话id
                Long conversationId = privateConversationService.insertOrUpdateConversationAndReturnConversationId(conversation, iConversation);
                AssertUtil.notNull(conversationId, "Failed execute to insert or update conversations.");
                conversation.setId(conversationId);
                // 新增消息内容
                AssertUtil.isTrue(privateMessageService.save(privateMessage), "Failed execute to insert private message.");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });
        // 设置结果集
        message.setConversationId(conversation.getId());
        message.setMessageId(id.toString());
        if (Boolean.TRUE.equals(result)) {
            // 发送新增会话事件和消息事件
            message.setStatus(ImMessageState.SUCCESS.name);
            privateConversationService.sendAppendPrivateChatEvent(conversation, message.getFromUser());
            imEventListener.onPrivateChat(new PrivateChatEvent(message));
        } else {
            message.setStatus(ImMessageState.FAILED.name);
        }
        return message;
    }

    @Override
    public boolean sendAddFriendMessage(Long userId, Long friendId, String content) {
        Map<Long, ImUserInfoDTO> userInfoMap = userSettingService.selectFriendInfosByUserIdAndFriendId(userId, friendId);
        if (MapUtils.isEmpty(userInfoMap) || userInfoMap.size() != 2) {
            log.error("Send friend message error, because query friend user info map is empty.");
            return false;
        }
        // 构造入库消息体
        PrivateMessage privateMessage = PrivateMessage.of(this.getDistributeMessageId(false), userId, friendId, EventMessageType.FRIEND.type, content, new Date());
        privateMessage.setIsRead(true);
        PrivateMessage noticeAddMessage = PrivateMessage.of(this.getDistributeMessageId(false), userId, friendId, EventMessageType.ADD_FRIEND_NOTICE.type, StrUtil.EMPTY, new Date());
        noticeAddMessage.setIsRead(true);

        List<PrivateConversation> privateConversations = transactionTemplate.execute(status -> {
            try {
                // 新增消息内容
                AssertUtil.isTrue(privateMessageService.saveBatch(List.of(privateMessage, noticeAddMessage)), "Failed execute to insert private message.");
                // 构建消息会话并且返回双方会话
                return privateConversationService.insertOrUpdateAddFriendConversations(userId, friendId, noticeAddMessage, userInfoMap);
            } catch (Throwable cause) {
                log.error(cause.getMessage(), cause);
                status.setRollbackOnly();
                return Collections.emptyList();
            }
        });

        if (CollectionUtils.isNotEmpty(privateConversations)) {
            privateConversations.forEach(privateConversation -> {
                // 发送新增会话事件
                Long contactId = privateConversation.getContactId();
                ImUserInfoDTO info = userInfoMap.get(contactId);
                UserInfoVO fromUser = UserInfoVO.of(contactId, info.getUsername(), info.getNickname(), info.getAvatar());
                privateConversationService.sendAppendPrivateChatEvent(privateConversation, fromUser);
                if (contactId.equals(friendId)) {
                    // 发送添加好友消息
                    ImMessageVO message = MessageFactory.create(privateMessage, fromUser);
                    imEventListener.onPrivateChat(new PrivateChatEvent(message));
                }
                // 发送添加好友提示消息
                ImMessageVO noticeMessage = MessageFactory.create(noticeAddMessage, fromUser);
                noticeMessage.setToContactId(contactId.toString());
                imEventListener.onPrivateChat(new PrivateChatEvent(noticeMessage));
            });
        }
        return false;
    }

    @Override
    public ImMessageVO sendGroupMessage(Long userId, Long groupId, ImMessageDTO message) {
        // 获取群聊用户列表
        Set<Long> groupMembers = groupService.getGroupMembers(groupId);
        // 获取消息类型
        Integer messageType = getMessageType(message);
        // 获取分布式群聊聊天记录id
        Long id = this.getDistributeMessageId(true);
        // 构建群聊聊天实体
        GroupMessage groupMessage = GroupMessage.of(id, userId, groupId, messageType, message);

        Boolean execute = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(groupMessageService.save(groupMessage), "Failed execute to insert group message.");
                AssertUtil.isTrue(groupConversationService.updateGroupConversations(groupMembers, groupId, messageType, message), "Failed execute tp update group conversations.");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });

        message.setMessageId(id.toString());
        message.setStatus(ImMessageState.SUCCESS.name);
        message.setIsRead(false);

        if (Boolean.TRUE.equals(execute)) {
            // 发送群聊消息事件
            imEventListener.onGroupChat(new GroupChatEvent(groupMembers.stream().map(Objects::toString).collect(Collectors.toSet()), message));
        } else {
            message.setStatus(ImMessageState.FAILED.name);
        }
        return message;
    }


    @Override
    public boolean addEventMessage(boolean group, UserInfoVO sender, Long contactId, Collection<Long> receives, EventMessageType messageType) {
       return addEventMessage(group, sender, contactId, receives, messageType, null);
    }

    @Override
    public boolean addEventMessage(boolean group, UserInfoVO sender, Long contactId, Collection<Long> receives, EventMessageType messageType, Date date) {
        // 构建消息体
        Long messageId = getDistributeMessageId(group);
        Long senderId = Long.valueOf(sender.getId());
        // 获取消息内容
        String content = JsonUtil.toJson(new ImMessageEventContentDTO(senderId, sender.getNickname()));
        boolean result;
        if (group) {
            GroupMessage groupMessage = GroupMessage.of(messageId, senderId, contactId, messageType.type, content, date);
            result = groupMessageService.save(groupMessage);
        } else {
            PrivateMessage privateMessage = PrivateMessage.of(messageId, senderId, contactId, messageType.type, content, date);
            result = privateMessageService.save(privateMessage);
        }
        if (CollectionUtils.isNotEmpty(receives) && result) {
            if (group) {
                // 消息接收者接收到的消息内容
                ImMessageDTO otherMessage = null;
                Map<Long, ImMessageDTO> messages = Maps.newHashMapWithExpectedSize(receives.size());
                for (Long receive : receives) {
                    if (receive.equals(senderId)) {
                        messages.put(receive, MessageFactory.create(true, senderId, sender, messageId, contactId, messageType.type, messageType.contentType, content, date));
                    } else {
                        if (otherMessage == null) {
                            otherMessage = MessageFactory.create(true, contactId, sender, messageId, contactId, messageType.type, messageType.contentType, content, date);
                        }
                        messages.put(receive, otherMessage);
                    }
                }
                MessageEventGroupChatEvent event = new MessageEventGroupChatEvent(messages);
                imEventListener.onMessageEventGroupChat(event);
            } else {
                for (Long receive : receives) {
                    ImMessageDTO message = MessageFactory.create(false, receive, sender, messageId, receive, messageType.type, messageType.contentType, content, date);
                    PrivateChatEvent privateChatEvent = new PrivateChatEvent(message);
                    imEventListener.onPrivateChat(privateChatEvent);
                }
            }
        }
        return result;
    }

    @Override
    public List<String> readPrivateMessages(Long userId, Long contactId) {
        List<Long> unreadMessageIds = privateMessageService.selectUnreadMessageIds(userId, contactId);
        if (CollectionUtils.isEmpty(unreadMessageIds)) {
            return Collections.emptyList();
        }
        boolean result = privateMessageService.readMessages(unreadMessageIds);
        if (!result) {
            log.error("Failed execute to read private messages: {}", unreadMessageIds);
            return Collections.emptyList();
        }
        return unreadMessageIds.stream().map(Objects::toString).toList();
    }

    @Override
    public List<String> readGroupMessages(GroupConversation groupConversation) {
        List<Long> unreadMessageIds = groupMessageService.selectUnreadMessageIds(groupConversation.getUserId(), groupConversation.getLastReadTime(), groupConversation.getGroupId());
        if (CollectionUtils.isEmpty(unreadMessageIds)) {
            return Collections.emptyList();
        }
        groupConversation.setLastReadTime(SystemClock.now());
        boolean update = groupConversationService.updateById(groupConversation);
        if (!update) {
            log.error("Failed execute to read group messages: {}", unreadMessageIds);
            return Collections.emptyList();
        }
        return unreadMessageIds.stream().map(Objects::toString).toList();
    }

    @Override
    public String undoMessage(boolean isGroup, Long userId, Long contactId, Long id, String messageId, Date created) {
        // 撤回人昵称
        String nickname = isGroup ? userSettingService.selectImUserNickname(userId) : StrUtil.EMPTY;
        // 消息撤回， 更改原消息内容
        String content = isGroup ? StrUtil.EMPTY : JsonUtil.toJson(new ImMessageEventContentDTO(userId, nickname));
        Boolean execute = transactionTemplate.execute(status -> {
            try {
                if (isGroup) {
                    AssertUtil.isTrue(groupConversationService.updateConversationUndoMessage(userId, contactId, content), "Failed execute update group conversation by undoMessage.");
                    AssertUtil.isTrue(groupMessageService.undoMessage(id, content), "Failed execute to undo group message.");
                } else {
                    AssertUtil.isTrue(privateConversationService.updateConversationUndoMessage(userId, contactId, content), "Failed execute update group conversation by undoMessage.");
                    AssertUtil.isTrue(privateMessageService.undoMessage(id, content), "Failed execute to undo group message.");
                }
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return false;
            }
        });

        if (Boolean.TRUE.equals(execute)) {
            // 异步发送撤回消息事件给用户
            IExecutorsRepository.newExecutor(Constants.IM_EXECUTOR_NAME).execute(() -> {
                // 消息撤回的内容.
                String undoMessageNotifyContent = isGroup ? ConvertUtil.getReplaceValue(ImLanguageContext.getValue(EventMessageType.UNDO.translateKey), nickname) :
                        ImLanguageContext.getValue(PropertiesConstants.UNDO_MESSAGE_BY_FRIEND);
                // 如果是群聊， 则需要发送撤回事件给所有的群聊用户. 私聊只需通知消息的接收者即可
                UndoMessageEvent event;
                if (isGroup) {
                    List<String> notifyUsers = groupService.getGroupMembers(contactId).stream().map(Objects::toString).toList();
                    event = UndoMessageEvent.of(notifyUsers, true, contactId, id, messageId,  undoMessageNotifyContent, created);
                } else {
                    event = UndoMessageEvent.of(List.of(contactId.toString()), false, userId, id, messageId, undoMessageNotifyContent, created);
                }
                imEventListener.onImUndoMessageEvent(event);
            });
            // 返回撤回内容。
            return ImLanguageContext.getValue(PropertiesConstants.UNDO_MESSAGE_BY_YOURSELF);
        }
        return StrUtil.EMPTY;
    }

    @Override
    public List<ImMessageVO> forwardMessage(Long accountId, String content, Integer type, ForwardMessageDTO forwardMessage) {
        List<ForwardMessageDTO.Forward> forwards = forwardMessage.getForwards();
        List<ImMessageVO> result = new ArrayList<>(forwards.size());
        String messageType = MessageType.getMessageType(type);
        // 可以发消息的联系人
        List<ForwardMessageDTO.Forward> enabledForwards = new ArrayList<>();
        forwards.forEach(forward -> {
            ImMessageDTO imMessageDTO = forward.getMessage();
            imMessageDTO.setContent(content);
            imMessageDTO.setType(messageType);
            AppsResultCode code = getEnableChatState(accountId, forward.getContactId(), forward.getGroup());
            if (code.isSuccess()) {
                enabledForwards.add(forward);
            } else {
                imMessageDTO.setStatus(ImMessageState.FAILED.name);
                result.add(imMessageDTO);
            }
        });

        if (CollectionUtils.isEmpty(enabledForwards)) {
            return result;
        }
        // 根据是否是群聊进行分类
        Map<Boolean, List<ForwardMessageDTO.Forward>> map = enabledForwards.stream().collect(Collectors.groupingBy(ForwardMessageDTO.Forward::getGroup));
        List<ForwardMessageDTO.Forward> privateForwards = map.get(Boolean.TRUE);
        List<PrivateConversation> privateConversations = getForwardPrivateConversations(privateForwards, accountId, type);
        List<PrivateMessage> privateMessages = getForwardPrivateMessages(privateForwards, accountId, content, type);
        List<ForwardMessageDTO.Forward> groupForwards = map.get(Boolean.FALSE);
        Map<Long, List<Long>> groupMemberMap = getGroupMemberMap(groupForwards);
        List<GroupConversation> groupConversations = getForwardGroupConversations(groupForwards, content, type, groupMemberMap);
        List<GroupMessage> groupMessages = getForwardGroupMessages(groupForwards, accountId, content, type);

        // 入库
        Boolean execute = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(privateConversationService.insertOrUpdate(privateConversations), "Failed execute to insert or update private conversations.");
                AssertUtil.isTrue(privateMessageService.saveBatch(privateMessages), "Failed execute to save batch private messages.");
                AssertUtil.isTrue(groupMessageService.saveBatch(groupMessages), "Failed execute to save batch group messages.");
                AssertUtil.isTrue(groupConversationService.insertOrUpdate(groupConversations), "Failed execute to insert or update group conversations.");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return false;
            }
        });

        if (Boolean.FALSE.equals(execute)) {
            enabledForwards.forEach(forward -> {
                ImMessageDTO message = forward.getMessage();
                message.setStatus(ImMessageState.FAILED.name);
                result.add(message);
            });
        } else {
            // 发送事件
           sendForwardMessageEvent(privateForwards, groupForwards, groupMemberMap);
        }
        return result;
    }

    @Override
    public boolean deleteGroupMessages(Long groupId) {
        return false;
    }

    private List<GroupMessage> getForwardGroupMessages(List<ForwardMessageDTO.Forward> groupForwards, Long accountId, String content, Integer type) {
        if (CollectionUtils.isEmpty(groupForwards)) {
            return Collections.emptyList();
        }
        return groupForwards.stream().map(forward -> {
            Long groupId = forward.getContactId();
            ImMessageDTO message = forward.getMessage();
            Long messageId = getDistributeMessageId(true);
            return GroupMessage.of(messageId, accountId, groupId, type, message);
        }).collect(Collectors.toList());
    }


    private List<GroupConversation> getForwardGroupConversations(List<ForwardMessageDTO.Forward> groupForwards, String content, Integer type, Map<Long, List<Long>> groupMemberMap) {
        if (CollectionUtils.isEmpty(groupForwards)) {
            return Collections.emptyList();
        }
        List<GroupConversation> conversations = new ArrayList<>();
        for (ForwardMessageDTO.Forward forward : groupForwards) {
            Long groupId = forward.getContactId();
            ImMessageDTO message = forward.getMessage();
            List<Long> members = groupMemberMap.get(groupId);
            List<GroupConversation> groupConversations = members.stream().map(member -> GroupConversation.builder()
                    .groupId(groupId)
                    .userId(member)
                    .lastMessageType(type)
                    .lastMessageTime(message.getSendTime() == null ? SystemClock.now() : message.getSendTime())
                    .lastMessageContent(content).build()).toList();
            conversations.addAll(groupConversations);
        }
        return conversations;
    }

    private Map<Long, List<Long>> getGroupMemberMap(List<ForwardMessageDTO.Forward> groupForwards) {
        if (CollectionUtils.isEmpty(groupForwards)) {
            return MapUtil.empty();
        }
        Set<Long> groupIds = groupForwards.stream().map(ForwardMessageDTO.Forward::getContactId).collect(Collectors.toSet());
        return groupService.getGroupMemberIdMaps(groupIds);
    }

    private List<PrivateMessage> getForwardPrivateMessages(List<ForwardMessageDTO.Forward> privateForwards, Long accountId, String content, Integer type) {
        List<PrivateMessage> privateMessages = new ArrayList<>(privateForwards.size());
        for (ForwardMessageDTO.Forward privateForward : privateForwards) {
            ImMessageDTO message = privateForward.getMessage();
            Long distributeMessageId = getDistributeMessageId(false);
            message.setId(distributeMessageId.toString());
            privateMessages.add(PrivateMessage.of(distributeMessageId, accountId, privateForward.getContactId(), type, content, null));
        }
        return privateMessages;
    }

    private List<PrivateConversation> getForwardPrivateConversations(List<ForwardMessageDTO.Forward> privateForwards, Long accountId, Integer type) {
        List<PrivateConversation> privateConversations = new ArrayList<>();
        for (ForwardMessageDTO.Forward forward : privateForwards) {
            ImMessageDTO message = forward.getMessage();
            privateConversations.add(PrivateConversation.of(accountId, forward.getContactId(), type, message));
            privateConversations.add(PrivateConversation.of(forward.getContactId(), accountId, type, message));
        }
        return privateConversations;
    }

    private void sendForwardMessageEvent(List<ForwardMessageDTO.Forward> privateForwards, List<ForwardMessageDTO.Forward> groupForwards, Map<Long, List<Long>> groupMemberMap) {
        if (CollectionUtils.isNotEmpty(privateForwards)) {
            privateForwards.forEach(forward -> {
                ImMessageDTO message = forward.getMessage();
                PrivateChatEvent event = new PrivateChatEvent(message);
                imEventListener.onPrivateChat(event);
            });
        }

        if (CollectionUtils.isNotEmpty(groupForwards)) {
            groupForwards.forEach(forward -> {
                List<Long> ids = groupMemberMap.get(forward.getContactId());
                if (CollectionUtils.isNotEmpty(ids)) {
                    GroupChatEvent groupChatEvent = new GroupChatEvent(ids.stream().map(Objects::toString).collect(Collectors.toSet()), forward.getMessage());
                    imEventListener.onGroupChat(groupChatEvent);
                }
            });
        }

    }

    private Integer getMessageType(ImMessageDTO messageDTO) {
        String type = messageDTO.getType();
        MessageType messageType = MessageType.getMessageType(type);
        return Objects.requireNonNull(messageType).type;
    }




}
