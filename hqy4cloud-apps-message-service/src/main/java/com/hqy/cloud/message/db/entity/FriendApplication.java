package com.hqy.cloud.message.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import com.hqy.cloud.message.bind.enums.ImFriendApplicationState;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_im_friend_application")
@EqualsAndHashCode(callSuper = true)
public class FriendApplication extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请人ID
     */
    private Long apply;

    /**
     * 接收人ID
     */
    private Long receive;

    /**
     * 申请时候的备注
     */
    private String remark;

    /**
     * 当前申请记录状态
     */
    private Integer status;

    /**
     * 是否删除
     */
    private Boolean deleted;


    public static FriendApplication of(Long apply, Long receive, String remark) {
        return FriendApplication.builder()
                .apply(apply)
                .receive(receive)
                .remark(remark)
                .status(ImFriendApplicationState.UN_READ.state).build();
    }

    public static FriendApplication of(Long apply, Long receive, Integer status) {
        return FriendApplication.builder()
                .apply(apply)
                .receive(receive)
                .status(status).build();
    }






}
