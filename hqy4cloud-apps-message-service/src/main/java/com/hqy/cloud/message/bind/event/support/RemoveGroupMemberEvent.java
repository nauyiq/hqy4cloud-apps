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
 * @date 2023/10/7 10:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveGroupMemberEvent implements ImEvent {

    private List<String> users;
    private Payload payload;

    public static RemoveGroupMemberEvent of(List<String> users, String groupId, String userId, ImMessageVO messageVO) {
        return new RemoveGroupMemberEvent(users, new Payload(groupId, userId, messageVO));
    }

    public String message() {
        return JsonUtil.toJson(payload);
    }

    @Override
    public String name() {
        return "removeGroupMember";
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
