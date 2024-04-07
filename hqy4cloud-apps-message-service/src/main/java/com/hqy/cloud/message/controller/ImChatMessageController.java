package com.hqy.cloud.message.controller;

import com.hqy.cloud.apps.commom.result.AppsResultCode;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.Constants;
import com.hqy.cloud.message.bind.dto.*;
import com.hqy.cloud.message.bind.enums.MessageType;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.message.service.request.ImChatMessageRequestService;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.web.common.BaseController;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 聊天消息API 控制器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/4
 */
@Slf4j
@RestController
@RequestMapping("/im")
@RequiredArgsConstructor
public class ImChatMessageController extends BaseController {
    private final ImChatMessageRequestService requestService;

    /**
     * 分页查询聊天记录, 不根据关键词查询的
     * @param request HttpServletRequest
     * @param param   请求参数 {@link MessagesRequestParamDTO}
     * @return        R.
     */
    @GetMapping("/messages")
    public R<PageResult<ImMessageVO>> getMessages(HttpServletRequest request, MessagesRequestParamDTO param) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        // 检查参数
        if (param == null || param.getConversationId() == null || param.getIsGroup() == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.getMessages(id, param);
    }

    /**
     * 分页查询聊天记录，基于宽表查询，用于关键词查找所有的聊天记录等（包括群聊，私聊）
     * @param request HttpServletRequest
     * @param param   请求参数 {@link MessagesRequestParamDTO}
     * @return        R.
     */
    @GetMapping("/message/search")
    public R<PageResult<ImMessageVO>> searchMessages(HttpServletRequest request, MessagesRequestParamDTO param) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (param == null || param.getConversationId() == null || param.getIsGroup() == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        String type = param.getType();
        MessageType messageType = MessageType.getMessageType(type);
        if (StringUtils.isNotBlank(type) && messageType == null) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        return requestService.searchPageMessages(accountId, param.getConversationId(), param.getIsGroup(), messageType, param.getKeywords(), param.getPage(), param.getLimit());
    }

    /**
     * 发送纯文本消息
     * @param request HttpServletRequest.
     * @param message {@link ImMessageDTO}
     * @return        R.
     */
    @PostMapping("/message")
    public R<ImMessageVO> sendImMessage(HttpServletRequest request, @Valid @RequestBody ImMessageDTO message) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        // 必填参数为空
        if (message == null || message.checkParamsIsUndefined()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        if (!message.getFromUser().getId().equals(id.toString())) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        // 设置消息类型为纯文本类型
        return requestService.sendImMessage(id, message);
    }

    /**
     * 发送文件消息
     * @param request HttpServletRequest.
     * @param file    发送的文件file
     * @param message 消息json对象
     * @return        R.
     */
    @PostMapping("/file/message")
    public R<ImMessageVO> sendImFileMessage(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("message") String message) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        // 必填参数为空
        if (file == null || file.isEmpty() || StringUtil.isBlank(message)) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        ImMessageDTO messageData = JsonUtil.toBean(message, ImMessageDTO.class);
        if (messageData == null || messageData.checkParamsIsUndefined()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        // 判断是否是支持的文件类型
        String type = messageData.getType();
        if (!MessageType.isFileMessage(type) || !messageData.getFromUser().getId().equals(id.toString())) {
            return R.failed(ResultCode.ERROR_PARAM);
        }
        return requestService.sendImFileMessage(id, file, messageData);
    }

    /**
     * 设置某个会话的未读消息为已读, 并且放回已读消息id
     * @param request HttpServletRequest.
     * @param dto     请求参数
     * @return        R.
     */
    @PutMapping("/messages/read")
    public R<Boolean> readMessages(HttpServletRequest request, @RequestBody MessageUnreadDTO dto) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (dto == null || !dto.enabled()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.readMessages(id, dto);
    }

    /**
     * 撤回某条消息
     * @param request     HttpServletRequest.
     * @param undoMessage 请求参数
     * @return            R.
     */
    @PutMapping("/message/undo")
    public R<String> undoMessage(HttpServletRequest request, @RequestBody UndoMessageDTO undoMessage) {
        Long userId = getAccessAccountId(request);
        if (userId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (undoMessage == null || undoMessage.getIsGroup() == null || undoMessage.getId() == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.undoMessage(userId, undoMessage);
    }

    /**
     * 转发消息
     * @param request        HttpServletRequest.
     * @param forwardMessage 转发的消息
     * @return               R.
     */
    @PostMapping("/message/forward")
    public R<List<ImMessageVO>> forwardMessages(HttpServletRequest request, @RequestBody ForwardMessageDTO forwardMessage) {
        Long accountId = getAccessAccountId(request);
        if (accountId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        // 检查参数是否可用
        if (forwardMessage == null || forwardMessage.getMessageId() == null || CollectionUtils.isEmpty(forwardMessage.getForwards())) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        if (forwardMessage.getForwards().stream().anyMatch(forward -> !forward.enable())) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        // 转发的联系人不能超过五个
        if (forwardMessage.getForwards().size() > Constants.DEFAULT_MAX_FORWARD_MESSAGE_COUNT) {
            return R.failed(AppsResultCode.IM_FORWARD_SIZE_MAX);
        }
        return requestService.forwardMessages(accountId, forwardMessage);
    }




}
