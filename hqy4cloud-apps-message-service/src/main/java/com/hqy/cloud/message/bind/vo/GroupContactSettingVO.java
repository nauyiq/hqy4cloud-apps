package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 17:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupContactSettingVO {

    /**
     * 群聊用户角色
     */
    private Integer role;

    /**
     * 群聊邀请确认
     */
    private Boolean invite;

    /**
     * 群公告
     */
    private String notice;

    /**
     * 群聊创建者
     */
    private String creator;




}
