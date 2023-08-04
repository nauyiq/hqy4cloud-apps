package com.hqy.cloud.apps.blog.config;

import com.hqy.cloud.chatgpt.common.lang.ChatGptModel;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/1 17:29
 */
public interface Constants {

    int DEFAULT_SOCKET_PORT = 9010;
    String DEFAULT_SOCKET_CONTEXT_PATH = "/blog/websocket";
    String DEFAULT_CHATGPT_DEFAULT_TITLE = "聊天室（chatGPT）";
    List<String> DEFAULT_CHATGPT_MODELS = Collections.singletonList(ChatGptModel.GTP_3_5_TURBO.name);




}
