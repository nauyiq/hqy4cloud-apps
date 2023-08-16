package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/16 17:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendVO {

    private String id;
    private String avatar;
    private String displayName;
    private String username;


}
