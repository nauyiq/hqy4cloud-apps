package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactsDTO {

    /**
     * 申请消息未读
     */
    private Integer applicationUnread;
    private List<ContactDTO> contacts;

}
