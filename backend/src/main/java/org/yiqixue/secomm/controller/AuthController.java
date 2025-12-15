package org.yiqixue.secomm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.yiqixue.secomm.dto.ApiResponse;
import org.yiqixue.secomm.dto.auth.JwtResponse;
import org.yiqixue.secomm.dto.auth.LoginRequest;
import org.yiqixue.secomm.dto.auth.SignupRequest;
import org.yiqixue.secomm.entity.Role;
import org.yiqixue.secomm.entity.User;
import org.yiqixue.secomm.entity.UserRole;
import org.yiqixue.secomm.repository.RoleRepository;
import org.yiqixue.secomm.repository.UserRepository;
import org.yiqixue.secomm.repository.UserRoleRepository;
import org.yiqixue.secomm.security.JwtUtils;
import org.yiqixue.secomm.security.UserDetailsServiceImpl;
import org.yiqixue.secomm.security.UserPrincipal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService; // 添加UserDetailsService依赖

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求: {}", loginRequest.getUsername());

        // 1. 执行身份验证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        log.info("用户认证成功: {}, 用户ID: {}", userDetails.getUsername(), userDetails.getId());

        // 2. 使用UserDetailsService重新加载用户信息（确保角色被正确加载）
        UserDetails reloadedUserDetails = userDetailsService.loadUserByUsername(userDetails.getUsername());
        UserPrincipal reloadedUserPrincipal = (UserPrincipal) reloadedUserDetails;

        // 3. 从UserPrincipal的authorities中提取角色列表
        List<String> roles = reloadedUserPrincipal.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        log.info("从UserDetailsService获取的角色列表: {}", roles);

        // 4. 构建响应
        JwtResponse jwtResponse = new JwtResponse(
                jwt,
                reloadedUserPrincipal.getId(),
                reloadedUserPrincipal.getUsername(),
                reloadedUserPrincipal.getRealName(),
                reloadedUserPrincipal.getPhone(),
                reloadedUserPrincipal.getGender(),
                roles
        );

        return ResponseEntity.ok(ApiResponse.success("登录成功", jwtResponse));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        log.info("用户注册请求: {}, 角色: {}", signUpRequest.getUsername(), signUpRequest.getRoleCode());

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "用户名已存在"));
        }

        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "手机号已存在"));
        }

        // Create new user (不设置角色)
        User.UserBuilder userBuilder = User.builder()
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .realName(signUpRequest.getRealName())
                .phone(signUpRequest.getPhone())
                .gender(signUpRequest.getGender())
                .status(User.UserStatus.ACTIVE);

        // 如果注册为客户角色，设置审批状态为待审批
        if ("ROLE_USER".equals(signUpRequest.getRoleCode())) {
            userBuilder.approvalStatus(User.ApprovalStatus.PENDING);
        }

        User user = userBuilder.build();

        // 1. 先保存用户，获得ID
        user = userRepository.save(user);
        log.info("用户保存成功，用户ID: {}", user.getId());

        // 2. 查找角色
        Role userRole = roleRepository.findByRoleCode(signUpRequest.getRoleCode())
                .orElseThrow(() -> new RuntimeException("角色未找到: " + signUpRequest.getRoleCode()));

        // 3. 创建用户角色关联
        UserRole userRoleEntity = UserRole.builder()
                .userId(user.getId())
                .roleId(userRole.getId())
                .role(userRole)
                .build();

        // 4. 保存用户角色关联
        userRoleRepository.save(userRoleEntity);
        log.info("用户角色关联保存成功: 用户ID={}, 角色ID={}", user.getId(), userRole.getId());

        String message = "ROLE_USER".equals(signUpRequest.getRoleCode())
                ? "客户注册成功，请等待管理员审批"
                : "用户注册成功";

        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        log.info("获取所有角色列表");
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(roles));
    }
}