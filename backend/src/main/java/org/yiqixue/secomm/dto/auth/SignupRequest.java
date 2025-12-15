package org.yiqixue.secomm.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.yiqixue.secomm.entity.User;

import static org.yiqixue.secomm.entity.User.ApprovalStatus.APPROVED;
import static org.yiqixue.secomm.entity.User.ApprovalStatus.PENDING;

@Data
public class SignupRequest {
    @NotBlank(message = "用户名不能为空")
    @Email(message = "用户名必须是有效的邮箱地址")
    @Size(max = 100)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 50, message = "真实姓名长度必须在2-50个字符之间")
    private String realName;

    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的手机号码")
    private String phone;

    private User.Gender gender = User.Gender.M;
    
    @NotBlank(message = "角色不能为空")
    private String roleCode = "ROLE_USER"; // 默认为普通用户

    private User.ApprovalStatus approvalStatus = PENDING;
}
