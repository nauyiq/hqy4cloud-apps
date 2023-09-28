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
 * @date 2023/9/27 17:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupNoticeEvent implements ImEvent {
    private List<String> userIds;
    private Payload payload;

    public static GroupNoticeEvent of(List<String> userIds, String groupId, String notice, String editor) {
        return new GroupNoticeEvent(userIds, new Payload(groupId, notice, editor));
    }


    @Override
    public String name() {
        return "groupNoticeChange";
    }

    public String message() {
        return JsonUtil.toJson(this.payload);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private String groupId;
        private String notice;
        private String editor;
    }

}
