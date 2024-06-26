package com.hqy.cloud.apps.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 18:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountRegistryDTO extends AccountBaseRegistryDTO {

    @NotEmpty(message = "password cannot be empty.")
    private String password;

    @NotEmpty(message = "Validation code cannot be empty.")
    private String code;

}
