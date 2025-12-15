package org.yiqixue.secomm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.yiqixue.secomm.dto.ApiResponse;
import org.yiqixue.secomm.dto.user.AddressRequest;
import org.yiqixue.secomm.dto.user.UserProfileUpdateRequest;
import org.yiqixue.secomm.entity.Address;
import org.yiqixue.secomm.entity.User;
import org.yiqixue.secomm.security.UserPrincipal;
import org.yiqixue.secomm.service.UserService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        log.info("获取当前用户信息: 用户ID={}", userPrincipal.getId());
        
        User user = userService.getUserById(userPrincipal.getId());
        
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * 获取当前用户审批状态
     */
    @GetMapping("/approval-status")
    public ResponseEntity<ApiResponse<User.ApprovalStatus>> getApprovalStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        log.info("获取用户审批状态: 用户ID={}", userPrincipal.getId());
        
        User.ApprovalStatus status = userService.getUserApprovalStatus(userPrincipal.getId());
        
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    /**
     * 更新用户个人信息
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        
        log.info("更新用户个人信息: 用户ID={}", userPrincipal.getId());
        
        User updatedUser = userService.updateUserProfile(userPrincipal.getId(), request);
        
        return ResponseEntity.ok(ApiResponse.success("个人信息更新成功", updatedUser));
    }

    /**
     * 更新手机号
     */
    @PutMapping("/phone")
    public ResponseEntity<ApiResponse<String>> updatePhone(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String phone) {
        
        log.info("更新用户手机号: 用户ID={}, 新手机号={}", userPrincipal.getId(), phone);
        
        userService.updateUserPhone(userPrincipal.getId(), phone);
        
        return ResponseEntity.ok(ApiResponse.success("手机号更新成功"));
    }

    /**
     * 获取用户所有地址
     */
    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<Address>>> getUserAddresses(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        log.info("获取用户地址列表: 用户ID={}", userPrincipal.getId());
        
        List<Address> addresses = userService.getUserAddresses(userPrincipal.getId());
        
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    /**
     * 更新送货地址
     */
    @PutMapping("/addresses/shipping")
    public ResponseEntity<ApiResponse<Address>> updateShippingAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddressRequest request) {
        
        log.info("更新送货地址: 用户ID={}", userPrincipal.getId());
        
        Address address = userService.updateUserAddress(userPrincipal.getId(), request, Address.AddressType.SHIPPING);
        
        return ResponseEntity.ok(ApiResponse.success("送货地址更新成功", address));
    }

    /**
     * 更新账单地址
     */
    @PutMapping("/addresses/billing")
    public ResponseEntity<ApiResponse<Address>> updateBillingAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddressRequest request) {
        
        log.info("更新账单地址: 用户ID={}", userPrincipal.getId());
        
        Address address = userService.updateUserAddress(userPrincipal.getId(), request, Address.AddressType.BILLING);
        
        return ResponseEntity.ok(ApiResponse.success("账单地址更新成功", address));
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long addressId) {
        
        log.info("删除用户地址: 用户ID={}, 地址ID={}", userPrincipal.getId(), addressId);
        
        userService.deleteUserAddress(userPrincipal.getId(), addressId);
        
        return ResponseEntity.ok(ApiResponse.success("地址删除成功"));
    }
}