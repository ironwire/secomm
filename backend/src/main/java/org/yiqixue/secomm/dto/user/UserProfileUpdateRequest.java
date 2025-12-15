package org.yiqixue.secomm.dto.user;

import lombok.Data;
import org.yiqixue.secomm.entity.User;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class UserProfileUpdateRequest {
    
    @Size(min = 2, max = 50, message = "真实姓名长度必须在2-50个字符之间")
    private String realName;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的手机号码")
    private String phone;
    
    private User.Gender gender;
    
    private String avatarUrl;
}