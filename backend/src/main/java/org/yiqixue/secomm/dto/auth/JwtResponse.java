package org.yiqixue.secomm.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.yiqixue.secomm.entity.User;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private User.Gender gender;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String realName, 
                      String phone, User.Gender gender, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.realName = realName;
        this.phone = phone;
        this.gender = gender;
        this.roles = roles;
    }
}