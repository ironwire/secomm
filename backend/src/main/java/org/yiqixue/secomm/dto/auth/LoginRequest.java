package org.yiqixue.secomm.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    @Email(message = "用户名必须是有效的邮箱地址")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}