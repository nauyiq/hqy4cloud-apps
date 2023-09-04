package com.hqy.cloud.message.controller;

import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import com.hqy.cloud.message.bind.dto.ImMessageDTO;
import com.hqy.cloud.message.bind.dto.MessageUnreadDTO;
import com.hqy.cloud.message.bind.dto.MessagesRequestParamDTO;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.service.request.ImMessageRequestService;
import com.hqy.cloud.web.common.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.undoMessage(id, messageId);
    }







}
