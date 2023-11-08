package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.message.bind.vo.ImMessageVO;
import com.hqy.cloud.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/10/9 16:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteGroupEvent implements ImEvent {

    private List<String> users;
    private Payload payload;

    public String message() {
        return JsonUtil.toJson(payload);
    }

    public static DeleteGroupEvent of(List<String> users, String groupId, String userId, ImMessageVO message) {
        return new DeleteGroupEvent(users, new Payload(groupId, userId, message));
    }

    @Override
    public String name() {
        return "deleteGroup";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private String groupId;
        private String userId;
        private ImMessageVO message;
    }


}
