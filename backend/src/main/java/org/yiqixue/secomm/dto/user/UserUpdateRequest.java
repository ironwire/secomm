package org.yiqixue.secomm.dto.user;

import lombok.Data;
import org.yiqixue.secomm.entity.User;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class UserUpdateRequest {
    
    @Size(min = 2, max = 50, message = "真实姓名长度必须在2-50个字符之间")
    private String realName;
    
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    private User.ApprovalStatus approvalStatus;
    
    private Boolean active;
}