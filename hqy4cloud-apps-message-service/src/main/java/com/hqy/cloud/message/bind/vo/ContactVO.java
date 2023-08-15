package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 14:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactVO {

    /**
     * 联系人id
     */
    private String id;

    /**
     * 展示用户名
     */
    private String displayName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 通讯录索引，传入字母或数字进行排序，索引可以显示自定义文字“[1]群组
     */
    private String index;

    /**
     * 是否是群
     */
    private Boolean isGroup;

    /**
     * 是否消息提醒
     */
    private Boolean isNotice;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 未读消息数
     */
    private Integer unread;

    /**
     * 群聊设置
     */
    private GroupContactSettingVO setting;

    /**
     * 最后一条消息类型
     */
    private String type;

    /**
     * 最近一条消息的时间戳，13位毫秒
     */
    private Long lastSendTime;

    /**
     * 最近一条消息的内容
     */
    private String lastContent;

}
