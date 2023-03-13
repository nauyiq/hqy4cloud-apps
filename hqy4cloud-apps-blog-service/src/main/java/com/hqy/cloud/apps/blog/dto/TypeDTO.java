package com.hqy.cloud.apps.blog.dto;

import com.hqy.cloud.common.base.lang.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/4 15:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeDTO {

    private Integer id;
    @NotEmpty(message = StringConstants.SHOULD_NOT_BE_EMPTY)
    private String name;
    @NotNull(message = StringConstants.SHOULD_NOT_BE_NULL)
    private Boolean status;


}
