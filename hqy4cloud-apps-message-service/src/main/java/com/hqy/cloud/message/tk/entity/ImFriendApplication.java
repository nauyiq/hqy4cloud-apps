package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
public class ImFriendApplication extends BaseEntity<Long> {
    public static final int NOT_VERIFY = 0;
    public static final int AGREE = 1;
    public static final int REFUSE = 2;

    private Long id;
    private Long apply;
    private Long receive;
    private String remark;
    private Integer status;
    private Date created;
    private Date updated;

    public ImFriendApplication(Long id, Integer status) {
        this.id = id;
        this.status = status;
    }

    public ImFriendApplication(Long apply, Long receive) {
        this.apply = apply;
        this.receive = receive;
    }

    public static ImFriendApplication of(Long id, Integer status) {
        return new ImFriendApplication(id, status);
    }

    public static ImFriendApplication of(Long apply, Long receive) {
        return new ImFriendApplication(apply, receive);
    }

    public static ImFriendApplication of(Long apply, Long receive, String remark, Integer status) {
        ImFriendApplication application = new ImFriendApplication();
        application.setReceive(receive);
        application.setApply(apply);
        application.setRemark(remark);
        application.setStatus(status);
        Date now = new Date();
        application.setCreated(now);
        application.setUpdated(now);
        return application;
    }





}
