package com.hqy.cloud.message.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 黑名单表
 * </p>
 *
 * @author qiyuan.hong
 * @since 2024-03-04
 */
@Data
@Builder
@TableName("t_im_blacklist")
@EqualsAndHashCode(callSuper = true)
public class Blacklist extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 黑明单用户id
     */
    private Long blackId;

}
