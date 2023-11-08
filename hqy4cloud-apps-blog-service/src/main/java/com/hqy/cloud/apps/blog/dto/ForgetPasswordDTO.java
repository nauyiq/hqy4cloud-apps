package com.hqy.cloud.apps.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * ForgetPasswordDTO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/19 13:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordDTO {

    @NotEmpty(message = "The username or email should not be empty.")
    private String usernameOrEmail;

    @NotEmpty(message = "The new password should not be empty.")
    private String password;

    @NotEmpty(message = "The email validation code should not be empty.")
    private String code;

}
