package com.hqy.cloud.apps.blog.dto;

import com.hqy.cloud.apps.blog.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/13 14:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentDTO {

    /**
     * 父级评论id
     */
    private Long parent;

    /**
     * 子集评论列表
     */
    private List<Comment> comments;

}
