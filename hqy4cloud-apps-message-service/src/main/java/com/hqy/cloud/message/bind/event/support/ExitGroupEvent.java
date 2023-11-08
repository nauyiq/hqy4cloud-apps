package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/10/9 14:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExitGroupEvent implements ImEvent {

    private List<String> users;
    private Payload payload;

    public static ExitGroupEvent of(List<String> users, String groupId, String userId) {
        return new ExitGroupEvent(users, new Payload(groupId, userId));
    }

    public String message() {
        return JsonUtil.toJson(payload);
    }

    @Override
    public String name() {
        return "exitGroup";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private String groupId;
        private String userId;
    }

}


