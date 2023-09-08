package com.hqy.cloud.message.controller;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.dto.ImChatConfigDTO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.service.request.ImConversationRequestService;
import com.hqy.cloud.web.common.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 聊天联系人相关API
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 16:39
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/im")
public class ImConversationController extends BaseController {
    private final ImConversationRequestService requestService;

    /**
     * 获取当前用户的聊天会话
     * @param request HttpServletRequest.
     * @return        R.
     */
    @GetMapping("/conversations")
    public R<List<ConversationVO>> getConversations(HttpServletRequest request) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getConversations(id);
    }

    /**
     * 新增会话, 需要新增会话的用户是你的好友或者允许陌生人聊天
     * @param request HttpServletRequest.
     * @param userId  用户id
     * @return        R.
     */
    @PostMapping("/conversation/{userId}")
    public R<ConversationVO> addConversation(HttpServletRequest request, @PathVariable("userId") Long userId) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.addConversation(id, userId);
    }

    /**
     * update chat top.
     * @param request    HttpServletRequest.
     * @param chatConfig {@link ImChatConfigDTO}
     * @return           R.
     */
    @PutMapping("/chat/top")
    public R<Boolean> updateChatTop(HttpServletRequest request, @RequestBody ImChatConfigDTO chatConfig) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (chatConfig == null || !chatConfig.isEnabled()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.updateChatTop(id, chatConfig);
    }

    /**
     * update chat notice.
     * @param request    HttpServletRequest.
     * @param chatConfig {@link ImChatConfigDTO}
     * @return           R.
     */
    @PutMapping("/chat/notice")
    public R<Boolean> updateChatNotice(HttpServletRequest request, @RequestBody ImChatConfigDTO chatConfig) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (chatConfig == null || !chatConfig.isEnabled()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.updateChatNotice(id, chatConfig);
    }





}
