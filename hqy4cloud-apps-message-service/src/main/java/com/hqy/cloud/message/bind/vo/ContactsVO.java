package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Integer inviteMessages;
    private List<ContactVO> contacts;

}
