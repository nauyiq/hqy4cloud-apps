package com.hqy.cloud.message.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 好友状态表
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_im_friend_state")
@EqualsAndHashCode(callSuper = true)
public class FriendState extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 好友状态表
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 好友id
     */
    private Long friendId;

    /**
     * 好友备注
     */
    private String remark;


    /**
     * 是否消息置顶
     */
    @TableField(value = "is_top")
    private Boolean top;

    /**
     * 是否消息通知
     */
    @TableField(value = "is_notice")
    private Boolean notice;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否删除
     */
    private Integer deleted;


    public static FriendState of(Long userId, Long friendId, Integer status, String remark) {
        return FriendState.builder()
                .userId(userId)
                .friendId(friendId)
                .top(false)
                .notice(true)
                .remark(remark)
                .status(status)
                .deleted(0).build();
    }



}
