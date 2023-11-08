package com.hqy.cloud.apps.blog.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 11:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_chatgpt_role")
public class ChatgptRole extends BaseEntity<Long> {

    private String key;
    private Integer sort;
    private String name;
    private String helloMessage;
    private String icon;
    private String context;
    private Boolean status;

}
