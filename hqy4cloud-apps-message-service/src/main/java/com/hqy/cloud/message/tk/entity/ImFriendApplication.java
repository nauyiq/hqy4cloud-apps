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
 * @date 2023/8/11 11:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_im_friend_application")
public class ImFriendApplication implements PrimaryLessBaseEntity {
    public static final int NOT_VERIFY = 0;
    public static final int AGREE = 1;
    public static final int REFUSE = 2;

    private Long id;
    private Long userId;
    private String remark;
    private Integer status;
    private Date created;
    private Date updated;

    public ImFriendApplication(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public static ImFriendApplication of(Long id, Long userId) {
        return new ImFriendApplication(id, userId);
    }

    public static ImFriendApplication of(Long id, Long userId, String remark) {
        ImFriendApplication application = new ImFriendApplication();
        application.setId(id);
        application.setUserId(userId);
        application.setRemark(remark);
        application.setStatus(NOT_VERIFY);
        Date now = new Date();
        application.setCreated(now);
        application.setUpdated(now);
        return application;
    }





}
