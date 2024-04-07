package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.event.ImEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 好友申请事件
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/18 10:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendApplicationEvent implements ImEvent {

    private String to;

    public static FriendApplicationEvent of(String to) {
        return new FriendApplicationEvent(to);
    }

    @Override
    public String name() {
        return "friendApplication";
    }

}
