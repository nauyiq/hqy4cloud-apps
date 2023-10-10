package com.hqy.cloud.message.bind.event.support;

import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.message.bind.vo.GroupMemberVO;
import com.hqy.cloud.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/10/7 10:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddGroupMemberEvent implements ImEvent {

    private List<String> users;
    private Payload payload;

    public static AddGroupMemberEvent of(List<String> users, String groupId, List<GroupMemberVO> members) {
        return new AddGroupMemberEvent(users, new Payload(groupId, members));
    }

    @Override
    public String name() {
        return "addGroupMember";
    }

    public String message() {
        return JsonUtil.toJson(payload);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Payload {
        private String groupId;
        private List<GroupMemberVO> users;
    }
}
