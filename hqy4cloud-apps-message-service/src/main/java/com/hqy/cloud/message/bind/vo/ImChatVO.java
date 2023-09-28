package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/22 10:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImChatVO {

    /**
     * 会话列表
     */
    private List<ConversationVO> conversations;

    /**
     * 联系人列表
     */
    private ContactsVO contacts;





}
