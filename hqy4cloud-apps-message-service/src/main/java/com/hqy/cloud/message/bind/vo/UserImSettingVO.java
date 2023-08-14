package com.hqy.cloud.message.bind.vo;

import com.hqy.cloud.message.tk.entity.ImUserSetting;
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

    public static UserImSettingVO of(ImUserSetting imUserSetting) {
        UserImSettingVO vo = new UserImSettingVO();
        vo.setIsPrivateChat(imUserSetting.getPrivateChat());
        vo.setIsInviteGroup(imUserSetting.getInviteGroup());
        vo.setIsOnline(imUserSetting.getOline());
        vo.setIsClearMsg(imUserSetting.getClearMsg());
        vo.setClearMessageDate(imUserSetting.getClearMsgDate());
        return vo;
    }

    public static UserImSettingVO of() {
        return new UserImSettingVO(false, true, true, true, 30);
    }





}
