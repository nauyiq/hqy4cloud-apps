package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/30 17:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactsVO {

    private Integer unread;
    private List<ContactVO> contacts;

    public static ContactsVO of() {
        return of(0, Collections.emptyList());
    }

    public static ContactsVO of(Integer unread, List<ContactVO> contacts) {
        return new ContactsVO(unread, contacts);
    }

}
