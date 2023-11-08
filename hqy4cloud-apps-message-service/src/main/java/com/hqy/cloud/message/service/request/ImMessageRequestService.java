package com.hqy.cloud.message.service.request;

import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.message.bind.dto.ForwardMessageDTO;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.bind.dto.MessagesRequestParamDTO;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26 13:19
 */
public interface ImMessageRequestService {

    /**
     * 获取聊天记录
     * @param id     用户id
     * @param params 请求参数
     * @return       R.
     */
    R<PageResult<ImMessageVO>> getImMessages(Long id, MessagesRequestParamDTO params);

    /**
     * send message to user or group
     * @param id      current user id.
     * @param message {@link ImMessageDTO}
     * @return        R.
     */
    R<ImMessageVO> sendImMessage(Long id, ImMessageDTO message);

    /**
     * 发送文件消息
     * @param id      用户id
     * @param file    发送的文件
     * @param message 消息
     * @return        R.
     */
    R<ImMessageVO> sendImFileMessage(Long id, MultipartFile file, ImMessageDTO message);

    /**
     * setting conversation messages is read.
     * @param id  user id
     * @param dto {@link MessageUnreadDTO}
     * @return    R.
     */
    R<List<String>> setMessageRead(Long id, MessageUnreadDTO dto);

    /**
     * undo message
     * @param authentication 登录用户认证信息
     * @param messageId      message id
     * @return               R
     */
    R<Boolean> undoMessage(AuthenticationInfo authentication, Long messageId);

    /**
     * 转发消息
     * @param id             转发人
     * @param forwardMessage 转发的消息
     * @return               R.
     */
    R<List<ImMessageVO>> forwardMessage(Long id, ForwardMessageDTO forwardMessage);


}
