package com.hqy.cloud.message.tk.entity;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.hqy.cloud.db.tk.PrimaryLessBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
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


    private Long id;
    private Long userId;
    private String remark;
    @Column(name = "`index`")
    private String index;
    @Column(name = "is_notice")
    private Boolean notice;
    @Column(name = "is_top")
    private Boolean top;
    private Boolean status;
    private Boolean deleted = false;
    private Date created;
    private Date updated;

    public ImFriend(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }
    public static List<ImFriend> addFriend(Long apply, Long receive, String mark, String applyNickname, String receiveNickname) {
        Date now = new Date();
        ImFriend from = new ImFriend(apply, receive, StrUtil.EMPTY, getIndex(receiveNickname), true, false, true, false, now, now);
        ImFriend to = new ImFriend(receive, apply, mark , getIndex(applyNickname), true, false, true, false, now, now);
        return Arrays.asList(from, to);
    }

    public static ImFriend of(Long id, Long userId) {
        return new ImFriend(id, userId);
    }

    private static String getIndex(String nickname) {
        if (StringUtils.isBlank(nickname)) {
            return "#";
        }
        char fistChar = nickname.charAt(0);
        String fistChatStr = Character.toString(fistChar);
        return (Validator.isWord(fistChatStr) || Validator.isNumber(fistChatStr)) ? PinyinUtil.getFirstLetter(fistChar) + StrUtil.EMPTY : "#";
    }
}
