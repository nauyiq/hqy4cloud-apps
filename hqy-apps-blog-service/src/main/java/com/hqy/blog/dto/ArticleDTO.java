package com.hqy.blog.dto;

import com.hqy.base.common.base.lang.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/30 15:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO {

    @NotEmpty(message = StringConstants.SHOULD_NOT_BE_EMPTY)
    private String title;
    @NotEmpty(message = StringConstants.SHOULD_NOT_BE_EMPTY)
    private String description;
    @NotNull(message = StringConstants.SHOULD_NOT_BE_NULL)
    private Integer type;
    @NotEmpty(message = StringConstants.SHOULD_NOT_BE_EMPTY)
    private String cover;
    private String musicName;
    private String musicUrl;
    @NotEmpty(message = StringConstants.SHOULD_NOT_BE_EMPTY)
    private String content;
    private Long author;



}
