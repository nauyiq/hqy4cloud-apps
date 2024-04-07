package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.message.bind.dto.*;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/5
 */
public interface ImChatMessageRequestService {

    /**
     * 分页查找聊天记录，不对关键词进行模糊查询
     * @param id    登录用户id
     * @param param 请求参数
     * @return      R
     */
    R<PageResult<ImMessageVO>> getMessages(Long id, MessagesRequestParamDTO param);

    /**
     * 分页查找聊天记录，根据关键词类型等查询
     * @param accountId      账号id
     * @param conversationId 会话id
     * @param isGroup        是否群聊
     * @param messageType    查找的聊天记录消息类型
     * @param keywords       关键词查找
     * @param page           第几页
     * @param limit          一页几行
     * @return               聊天记录
     */
    R<PageResult<ImMessageVO>> searchPageMessages(Long accountId, Long conversationId, Boolean isGroup, MessageType messageType, String keywords, Integer page, Integer limit);

    /**
     * 发送聊天消息
     * @param id      当前登录用户，发送人
     * @param message 消息体
     * @return        R
     */
    R<ImMessageVO> sendImMessage(Long id, ImMessageDTO message);

    /**
     * 发送文件消息
     * @param id      消息id
     * @param file    文件
     * @param message 消息体
     * @return        R
     */
    R<ImMessageVO> sendImFileMessage(Long id, MultipartFile file, ImMessageDTO message);

    /**
     * 设置消息为已读
     * @param id  用户id
     * @param dto 请求参数
     * @return    是否成功
     */
    R<Boolean> readMessages(Long id, MessageUnreadDTO dto);

    /**
     * 撤回消息
     * @param userId      用户id
     * @param undoMessage 撤回的消息
     * @return            是否成功
     */
    R<String> undoMessage(Long userId, UndoMessageDTO undoMessage);

    /**
     * 转发消息
     * @param accountId      登录人id
     * @param forwardMessage 转发的消息
     * @return               是否转发成功
     */
    R<List<ImMessageVO>> forwardMessages(Long accountId, ForwardMessageDTO forwardMessage);


}
