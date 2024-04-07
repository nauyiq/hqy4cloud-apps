package com.hqy.cloud.message.bind.enums;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/5
 */
public enum BlacklistState {

    /**
     * 互相没有拉黑
     */
    NONE(0),

    /**
     * 拉黑了对方
     */
    BLACKED_TO(1),

    /**
     * 对方拉黑你.
     */
    BLACKED_FROM(2),


    ;

    public final Integer state;


    BlacklistState(Integer state) {
        this.state = state;
    }

    public static BlacklistState of(Integer state) {
        if (state == null) {
            return null;
        }
        BlacklistState[] states = values();
        for (BlacklistState blacklistState : states) {
            if (blacklistState.state.equals(state)) {
                return blacklistState;
            }
        }
        return null;
    }


}
