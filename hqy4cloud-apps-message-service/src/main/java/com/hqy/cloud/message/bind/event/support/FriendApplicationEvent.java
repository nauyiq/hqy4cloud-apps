package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.util.JsonUtil;
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
    private Payload payload;

    public static FriendApplicationEvent of(String to, int unread) {
        return new FriendApplicationEvent(to, new Payload(unread));
    }

    public String message() {
        return JsonUtil.toJson(payload);
    }


    @Override
    public String name() {
        return "friendApplication";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private Integer unread;
    }

}
