package com.hqy.blog.dto;

import com.hqy.base.common.base.lang.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * 发表文章评论DTO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/11/1 15:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishCommentDTO {

    /**
     * 1级评论还是2级评论
     */
    @NotNull(message = StringConstants.SHOULD_NOT_BE_NULL)
    private Integer level;

    /**
     * 文章id
     */
    @NotEmpty(message = StringConstants.SHOULD_NOT_BE_EMPTY)
    private String articleId;

    /**
     * 评论的内容
     */
    @NotEmpty(message = StringConstants.SHOULD_NOT_BE_EMPTY)
    private String content;


    private String parentId;


    private String replier;






}
