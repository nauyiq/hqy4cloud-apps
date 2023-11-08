package com.hqy.cloud.message.bind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/20 16:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexFriendsVO {
    private String index;
    private List<FriendVO> friends;
}
