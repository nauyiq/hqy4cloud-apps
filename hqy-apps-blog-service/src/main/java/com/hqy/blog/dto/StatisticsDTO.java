package com.hqy.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hqy.blog.statistics.StatisticsType;
import lombok.*;

import java.io.Serializable;

/**
 * StatisticsDTO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/11 16:51
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class StatisticsDTO implements Serializable {


    private Long id;
    private Integer visits;
    private Integer likes;
    private Integer comments;

    public StatisticsDTO(Long id, Integer visits, Integer likes, Integer comments) {
        this.id = id;
        this.visits = visits;
        this.likes = likes;
        this.comments = comments;
    }

    /**
     * 表示当前id已经进行hash 并且找到正确位置的key
     */
    @JsonIgnore
    private transient int key;

    public StatisticsDTO(Long id) {
        this.id = id;
        this.visits = 0;
        this.likes = 0;
        this.comments = 0;
    }

    public StatisticsDTO(Long id, int key) {
        this.id = id;
        this.key = key;
        this.visits = 0;
        this.likes = 0;
        this.comments = 0;
    }

    public int getCount(StatisticsType type) {
        switch (type) {
            case VISITS:
                return this.visits;
            case LIKES:
                return this.likes;
            case COMMENTS:
                return this.comments;
            default:
                throw new IllegalArgumentException("Unknown statistics type for " + type);
        }
    }

    public void updateCount(StatisticsType type, int offset) {
        switch (type) {
            case VISITS:
                this.visits = getVisits() + offset;
                break;
            case LIKES:
                this.likes = getLikes() + offset;
                break;
            case COMMENTS:
                this.comments = getComments() + offset;
                break;
            default:
                throw new IllegalArgumentException("Unknown statistics type for " + type);
        }
    }

}
