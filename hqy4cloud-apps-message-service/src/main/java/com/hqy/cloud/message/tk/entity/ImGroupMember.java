package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.PrimaryLessBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_im_group_member")
public class ImGroupMember implements PrimaryLessBaseEntity {

    private Long groupId;
    private Long userId;
    private String displayName;
    private Integer role;
    private Boolean status;
    private Date created;
    private Date updated;

}
