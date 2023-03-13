package com.hqy.cloud.apps.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 17:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountBaseRegistryDTO {

    @NotEmpty(message = "Username cannot be empty.")
    private String username;

    @NotEmpty(message = "Email cannot be empty.")
    private String email;


}
