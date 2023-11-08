package com.hqy.cloud.message.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/18 11:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendApplicationDTO {
    private Long id;
    private Integer unread;
    private String nickname;
    private Integer status;

    public FriendApplicationDTO(Long id, Integer unread, Integer status) {
        this.id = id;
        this.unread = unread;
        this.status = status;
    }
}
