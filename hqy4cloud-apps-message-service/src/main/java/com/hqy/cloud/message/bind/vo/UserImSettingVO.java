package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @date 2023-08-12 11:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserImSettingVO {

    /**
     * 是否允许陌生人发消息
     */
    private Boolean isPrivateChat;

    /**
     * 是否允许邀请加入群聊
     */
    private Boolean isInviteGroup;

    /**
     * 是否显示在线状态
     */
    private Boolean isOnline;

    /**
     * 是否允许自动清除聊天记录
     */
    private Boolean isClearMsg;

    /**
     * 聊天记录保留天数.
     */
    private Integer clearMessageDate;






}
