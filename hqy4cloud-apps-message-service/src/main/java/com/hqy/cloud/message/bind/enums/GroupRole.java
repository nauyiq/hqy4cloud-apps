package com.hqy.cloud.message.bind.enums;

import java.util.Arrays;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/15 9:28
 */
public enum GroupRole {

    /**
     * 创建者
     */
    CREATOR("creator", 1),

    /**
     * 管理员
     */
    MANAGER("manager", 2),

    /**
     * 普通用户
     */
    COMMON("common", 3),

    /**
     * 被移除了的用户
     */
    REMOVED("removed", 4),

    ;

    public final String name;
    public final Integer role;

    GroupRole(String name, Integer role) {
        this.name = name;
        this.role = role;
    }

    public static boolean enableRole(int role) {
        return Arrays.stream(values()).anyMatch(v -> v.role == role);
    }
}
