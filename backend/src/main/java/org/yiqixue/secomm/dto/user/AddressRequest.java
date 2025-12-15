package org.yiqixue.secomm.dto.user;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class AddressRequest {
    
    @NotBlank(message = "国家不能为空")
    @Size(max = 50, message = "国家名称长度不能超过50个字符")
    private String country;
    
    @NotBlank(message = "省份不能为空")
    @Size(max = 50, message = "省份名称长度不能超过50个字符")
    private String state;
    
    @NotBlank(message = "城市不能为空")
    @Size(max = 50, message = "城市名称长度不能超过50个字符")
    private String city;
    
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过200个字符")
    private String street;
    
    @Size(max = 20, message = "邮政编码长度不能超过20个字符")
    private String zipCode;
}