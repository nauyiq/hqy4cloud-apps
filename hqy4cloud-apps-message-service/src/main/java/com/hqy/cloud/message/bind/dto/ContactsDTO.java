package com.hqy.cloud.message.bind.dto;

import com.hqy.cloud.message.tk.entity.ImFriend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/21 13:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactsDTO {

    private Integer unread;
    private List<ImFriend> contacts;


}
