package com.hqy.cloud.message.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 好友关系表
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_im_friend_relationship")
public class FriendRelationship extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 好友申请人ID
     */
    private Long apply;

    /**
     * 好友接受人ID
     */
    private Long receive;

    /**
     * 是否删除
     */
    private Integer deleted;

    public static FriendRelationship of(Long apply, Long receive) {
        return FriendRelationship.builder()
                .apply(apply)
                .receive(receive)
                .deleted(0).build();
    }


}
