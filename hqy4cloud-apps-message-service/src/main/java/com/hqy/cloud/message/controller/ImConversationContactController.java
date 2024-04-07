package com.hqy.cloud.message.controller;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.message.bind.dto.ContactDTO;
import com.hqy.cloud.message.bind.dto.ConversationDTO;
import com.hqy.cloud.message.bind.dto.ImChatConfigDTO;
import com.hqy.cloud.message.bind.vo.ContactsVO;
import com.hqy.cloud.message.bind.vo.ConversationVO;
import com.hqy.cloud.message.service.request.ImChatConversationRequestService;
import com.hqy.cloud.web.common.BaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 聊天会话、联系人相关API 控制器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/6
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/im")
public class ImConversationContactController extends BaseController {
    private final ImChatConversationRequestService requestService;

    /**
     * 获取用户聊天会话列表
     * @param request HttpServletRequest.
     * @return        R.
     */
    @GetMapping("/conversations")
    public R<List<ConversationVO>> getImUserConversations(HttpServletRequest request) {
        Long userId = getAccessAccountId(request);
        if (userId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getImUserConversations(userId);
    }

    /**
     * 获取联系人列表
     * @param request HttpServletRequest.
     * @return        R.
     */
    @GetMapping("/contacts")
    public R<ContactsVO> getImUserContacts(HttpServletRequest request) {
        Long userId = getAccessAccountId(request);
        if (userId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getImUserContacts(userId);
    }

    /**
     * 修改联系人聊天置顶设置
     * @param request    HttpServletRequest.
     * @param chatConfig 聊天设置
     * @return           R.
     */
    @PutMapping("/chat/top")
    public R<Boolean> updateChatTopState(HttpServletRequest request, @RequestBody ImChatConfigDTO chatConfig) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (chatConfig == null || !chatConfig.isEnabled()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.updateChatTopState(id, chatConfig);
    }

    /**
     * 修改联系人聊天消息提醒设置
     * @param request    HttpServletRequest.
     * @param chatConfig 聊天设置
     * @return           R.
     */
    @PutMapping("/chat/notice")
    public R<Boolean> updateChatNoticeState(HttpServletRequest request, @RequestBody ImChatConfigDTO chatConfig) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (chatConfig == null || !chatConfig.isEnabled()) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.updateChatNoticeState(id, chatConfig);
    }

    /**
     * 新增会话，用于删除会话后重新发起会话的场景
     * @param request HttpServletRequest.
     * @param params  请求参数
     * @return        R.
     */
    @PostMapping("/conversation")
    public R<ConversationVO> addConversation(HttpServletRequest request, @RequestBody ConversationDTO params) {
        Long id = getAccessAccountId(request);
        if (id == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (params == null || params.getContactId() == null || params.getIsGroup() == null) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.addConversation(id, params.getContactId(), params.getIsGroup());
    }

    /**
     * 删除好友会话
     * @param request        HttpServletRequest.
     * @param conversationId 会话id
     * @return               R.
     */
    @DeleteMapping("/conversation/friend/{id}")
    public R<Boolean> deleteFriendConversation(HttpServletRequest request, @PathVariable("id") Long conversationId) {
        Long userId = getAccessAccountId(request);
        if (userId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.deleteConversation(userId, false, conversationId);
    }

    @DeleteMapping("/conversation/group/{id}")
    public R<Boolean> deleteGroupConversation(HttpServletRequest request, @PathVariable("id") Long conversationId) {
        Long userId = getAccessAccountId(request);
        if (userId == null) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.deleteConversation(userId, true, conversationId);
    }








}
