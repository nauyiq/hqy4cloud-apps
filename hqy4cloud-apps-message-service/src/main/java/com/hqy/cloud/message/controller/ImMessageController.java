package com.hqy.cloud.message.controller;

import com.hqy.cloud.apps.commom.constants.AppsConstants;
import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.dto.ForwardMessageDTO;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.bind.dto.MessagesRequestParamDTO;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.service.request.ImMessageRequestService;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.authentication.AuthenticationRequestContext;
import com.hqy.cloud.web.common.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/1 13:38
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ImMessageController extends BaseController {
    private final ImMessageRequestService requestService;

    /**
     * 获取聊天记录
     * @param request HttpServletRequest
     * @param params  请求参数 {@link MessagesRequestParamDTO}
     * @return        R.
     */
    @GetMapping("/im/messages")
    public R<PageResult<ImMessageVO>> getImMessages(HttpServletRequest request, MessagesRequestParamDTO params) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (authentication == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (params.getConversationId() == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.getImMessages(authentication.getId(), params);
    }

    /**
     * send message to user or group.
     * @param request HttpServletRequest.
     * @param message {@link ImMessageDTO}
     * @return        R.
     */
    @PostMapping("/im/message")
    public R<ImMessageVO> sendImMessage(HttpServletRequest request, @RequestBody ImMessageDTO message) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (authentication == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        //check request params
        if (message == null || !message.checkParams()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.sendImMessage(authentication.getId(), message);
    }

    /**
     * 发送文件消息
     * @param request HttpServletRequest.
     * @param file    文件file
     * @param message 消息
     * @return        R.
     */
    @PostMapping("/im/file/message")
    public R<ImMessageVO> sendFileMessage(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("message") String message) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (authentication == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (StringUtils.isBlank(message) || file == null || file.isEmpty()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        ImMessageDTO messageData = JsonUtil.toBean(message, ImMessageDTO.class);
        if (messageData == null || !messageData.checkParams() || file.getSize() > AppsConstants.Message.IM_FILE_MESSAGE_MEX_SIZE) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        return requestService.sendImFileMessage(authentication.getId(), file, messageData);
    }

    /**
     * setting conversation messages is read.
     * @param request HttpServletRequest.
     * @param dto     {@link MessageUnreadDTO}
     * @return        R.
     */
    @PutMapping("/im/messages/read")
    public R<List<String>> setMessagesRead(HttpServletRequest request, @RequestBody MessageUnreadDTO dto) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (authentication == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (dto == null || !dto.isEnabled()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.setMessageRead(authentication.getId(), dto);
    }

    /**
     * undo a im message.
     * @param request   HttpServletRequest.
     * @param messageId message id.
     * @return          result.
     */
    @PutMapping("/im/message/undo/{id}")
    public R<Boolean> undoMessage(HttpServletRequest request, @PathVariable("id")Long messageId) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (authentication == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.undoMessage(authentication, messageId);
    }

    /**
     * 消息转发接口
     * @param request        HttpServletRequest.
     * @param forwardMessage {@link ForwardMessageDTO}
     * @return               R.
     */
    @PostMapping("/im/message/forward")
    public R<List<ImMessageVO>> forwardMessage(HttpServletRequest request, @RequestBody ForwardMessageDTO forwardMessage) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (forwardMessage == null || forwardMessage.getMessageId() == null || CollectionUtils.isEmpty(forwardMessage.getForwards())) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        if (forwardMessage.getForwards().stream().anyMatch(forward -> !forward.enable())) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        if (forwardMessage.getForwards().size() > 5) {
            return R.failed(AppsResultCode.IM_FORWARD_SIZE_MAX);
        }
        return requestService.forwardMessage(id, forwardMessage);
    }







}
