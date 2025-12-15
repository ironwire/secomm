package org.yiqixue.secomm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.yiqixue.secomm.dto.ApiResponse;
import org.yiqixue.secomm.dto.user.UserApprovalRequest;
import org.yiqixue.secomm.dto.user.UserUpdateRequest;
import org.yiqixue.secomm.entity.User;
import org.yiqixue.secomm.security.UserPrincipal;
import org.yiqixue.secomm.service.UserManagementService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
//@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    /**
     * 获取待审批的用户列表
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<User>>> getPendingUsers() {
        log.info("获取待审批用户列表");
        
        List<User> pendingUsers = userManagementService.getPendingUsers();
        
        return ResponseEntity.ok(ApiResponse.success(pendingUsers));
    }

    /**
     * 审批用户注册
     */
    @PostMapping("/{userId}/approve")
    public ResponseEntity<ApiResponse<String>> approveUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserApprovalRequest request,
            @AuthenticationPrincipal UserPrincipal adminPrincipal) {
        
        log.info("审批用户注册: 用户ID={}, 审批人={}, 审批结果={}",
                userId, adminPrincipal.getUsername(), request.getApprovalStatus());

        userManagementService.approveUser(userId, request, adminPrincipal.getId());

        String message = User.ApprovalStatus.APPROVED.equals(request.getApprovalStatus()) 
                ? "用户审批通过，已创建客户账户" 
                : "用户审批已拒绝";

        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        log.info("获取所有用户列表");
        
        List<User> users = userManagementService.getAllUsers();
        
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * 根据用户ID获取用户详情
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long userId) {
        log.info("获取用户详情: 用户ID={}", userId);
        
        User user = userManagementService.getUserById(userId);
        
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal adminPrincipal) {
        
        log.info("更新用户信息: 用户ID={}, 操作人={}", userId, adminPrincipal.getUsername());
        
        User updatedUser = userManagementService.updateUser(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("用户信息更新成功", updatedUser));
    }
}
