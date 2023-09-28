package com.hqy.cloud.message.bind.event.support;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.message.bind.event.ImEvent;
import com.hqy.cloud.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/8 16:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactNameChangeEvent implements ImEvent {
    private boolean isGroup;
    private List<String> users;
    private Payload payload;

    public static ContactNameChangeEvent of(boolean isGroup, List<String> users, String contact, String displayName) {
        return new ContactNameChangeEvent(isGroup, users, new Payload(contact, displayName, isGroup, StrUtil.EMPTY));
    }
    public static ContactNameChangeEvent of(boolean isGroup, List<String> users, String contact, String displayName, String editor) {
        return new ContactNameChangeEvent(isGroup, users, new Payload(contact, displayName, isGroup, editor));
    }


    @Override
    public String name() {
        return "contactNameChange";
    }

    public String messagePayload() {
        return JsonUtil.toJson(this.payload);
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private String contactId;
        private String displayName;
        private Boolean isGroup;
        private String editor;
    }
}
