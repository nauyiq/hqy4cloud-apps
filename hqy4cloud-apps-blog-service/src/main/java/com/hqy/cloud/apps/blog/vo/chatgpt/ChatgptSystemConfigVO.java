package com.hqy.cloud.apps.blog.vo.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.hqy.cloud.apps.blog.config.Constants.DEFAULT_CHATGPT_DEFAULT_TITLE;
import static com.hqy.cloud.apps.blog.config.Constants.DEFAULT_CHATGPT_MODELS;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 15:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatgptSystemConfigVO {

    private String title;
    private List<String> models;

    public static final ChatgptSystemConfigVO DEFAULT = new ChatgptSystemConfigVO(DEFAULT_CHATGPT_DEFAULT_TITLE, DEFAULT_CHATGPT_MODELS);



}
