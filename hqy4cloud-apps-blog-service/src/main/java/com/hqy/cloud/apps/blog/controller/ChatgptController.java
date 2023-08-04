package com.hqy.cloud.apps.blog.controller;

import com.hqy.cloud.apps.blog.entity.ChatgptConversation;
import com.hqy.cloud.apps.blog.service.request.ChatgptRequestService;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptConversationVO;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptMessageHistoryVO;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptRoleVO;
import com.hqy.cloud.apps.blog.vo.chatgpt.ChatgptSystemConfigVO;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 13:40
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog/chatgpt")
public class ChatgptController {
    private final ChatgptRequestService requestService;


    @GetMapping("/roles")
    public R<List<ChatgptRoleVO>> getEnableChatgptRoles(HttpServletRequest request) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (Objects.isNull(authentication)) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getEnableChatgptRoles();
    }

    @GetMapping("/conversations")
    public R<List<ChatgptConversationVO>> getChatgptConversations(HttpServletRequest request) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (Objects.isNull(authentication)) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getChatgptConversations(authentication.getId());
    }

    @PutMapping("/conversation")
    public R<Boolean> updateChatgptConversation(HttpServletRequest request, ChatgptConversation conversation) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (Objects.isNull(authentication)) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (Objects.isNull(conversation) || StringUtils.isAnyBlank(conversation.getId(), conversation.getTitle())) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        conversation.setUserId(authentication.getId());
        return requestService.updateChatgptConversation(conversation);
    }

    @DeleteMapping("/conversation/{chatId}")
    public R<Boolean> deleteChatgptConversation(@PathVariable("chatId") String chatId,  HttpServletRequest request) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (Objects.isNull(authentication)) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        if (StringUtils.isBlank(chatId)) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.deleteChatgptConversation(chatId, authentication.getId());
    }

    @DeleteMapping("/conversation/clear")
    public R<Boolean> clearChatgptConversations(HttpServletRequest request) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (Objects.isNull(authentication)) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.clearChatgptConversations(authentication.getId());
    }

    @GetMapping("/history/{chatId}")
    public R<List<ChatgptMessageHistoryVO>> getChatgptMessages(@PathVariable("chatId") String chatId, HttpServletRequest request) {
        if (StringUtils.isBlank(chatId)) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (Objects.isNull(authentication)) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getChatgptMessages(chatId, authentication.getId());
    }

    @GetMapping("/system/config")
    public R<ChatgptSystemConfigVO> getChatgptSystemConfig(HttpServletRequest request) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication(request);
        if (Objects.isNull(authentication)) {
            return R.failed(ResultCode.NOT_LOGIN);
        }
        return requestService.getChatgptSystemConfig();
    }












}
