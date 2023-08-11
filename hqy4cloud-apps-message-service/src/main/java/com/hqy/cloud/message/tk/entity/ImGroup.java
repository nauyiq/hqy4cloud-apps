package com.hqy.cloud.message.tk.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:25
 */
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_im_group")
public class ImGroup extends BaseEntity<Long> {

    private String name;
    private Long creator;
    private String notice;
    private Boolean status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
