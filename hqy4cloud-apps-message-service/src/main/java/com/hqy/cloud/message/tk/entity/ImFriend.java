package com.hqy.cloud.message.tk.entity;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.db.tk.PrimaryLessBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/11 11:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_im_friend")
public class ImFriend implements PrimaryLessBaseEntity {

    @Id
    private Long id;
    @Id
    private Long userId;
    private String remark;
    @Column(name = "`index`")
    private String index;
    @Column(name = "is_notice")
    private Boolean notice;
    @Column(name = "is_top")
    private Boolean top;
    private Boolean status;
    private Date created;
    private Date updated;

    public ImFriend(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public ImFriend(Long id, Long userId, boolean status) {
        this.id = id;
        this.userId = userId;
        this.status = status;
    }

    public static List<ImFriend> of(Long id, Long userId, String mark) {
        Date now = new Date();
        ImFriend from = new ImFriend(id, userId, mark, "", true, false, true, now, now);
        ImFriend to = new ImFriend(userId, id, StrUtil.EMPTY, "", true, false, true, now, now);
        return Arrays.asList(from, to);
    }

    public static ImFriend of(Long id, Long userId) {
        return new ImFriend(id, userId);
    }

    public static ImFriend of(Long id, Long userId, boolean status) {
        return new ImFriend(id, userId, status);
    }




}
